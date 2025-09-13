import json
import pandas as pd
import argparse
from pathlib import Path
from datetime import datetime

# This hierarchy defines which CWEs are considered "close matches" to a given CWE.
CWE_HIERARCHY = {
    "CWE-22": ["CWE-22", "CWE-23", "CWE-36"],  # Path Traversal variations
    "CWE-78": ["CWE-78", "CWE-77"],  # OS Command Injection variations
    "CWE-79": ["CWE-79", "CWE-80", "CWE-74"],  # Cross-Site Scripting (XSS) variations
    "CWE-89": ["CWE-89", "CWE-564", "CWE-943"],  # SQL Injection variations
    "CWE-90": ["CWE-90", "CWE-943"],  # LDAP Injection variations
    "CWE-327": ["CWE-327", "CWE-326", "CWE-328"],  # Cryptographic Issues
    "CWE-328": ["CWE-328", "CWE-327", "CWE-326", "CWE-916"],  # Cryptographic Issues
    "CWE-330": ["CWE-330", "CWE-338"],  # Cryptographic Issues
    "CWE-501": ["CWE-501"],
    "CWE-614": ["CWE-614"],
    "CWE-643": ["CWE-643"],
}


def calculate_metrics(df: pd.DataFrame) -> dict:
    """Calculates a dictionary of metrics from a results dataframe."""
    metrics = {}

    # Confusion Matrix sums
    tp = df["tp"].sum()
    fp = df["fp"].sum()
    tn = df["tn"].sum()
    fn = df["fn"].sum()
    metrics.update({"tp": tp, "fp": fp, "tn": tn, "fn": fn})

    # Performance metrics
    denominator_accuracy = tp + tn + fp + fn
    metrics["accuracy"] = (
        ((tp + tn) / denominator_accuracy) if denominator_accuracy > 0 else 0.0
    )

    denominator_precision = tp + fp
    metrics["precision"] = (
        (tp / denominator_precision) if denominator_precision > 0 else 0.0
    )

    denominator_recall = tp + fn
    metrics["recall"] = (tp / denominator_recall) if denominator_recall > 0 else 0.0

    denominator_f1 = metrics["precision"] + metrics["recall"]
    metrics["f1"] = (
        (2 * (metrics["precision"] * metrics["recall"]) / denominator_f1)
        if denominator_f1 > 0
        else 0.0
    )

    # Direct match percentage
    df_matched = df[df["matched_cwe"].notna() & (df["matched_cwe"] != "")].copy()
    if not df_matched.empty:
        direct_match_sum = df_matched["direct_match"].sum()
        total_with_match = len(df_matched)
        metrics["direct_match_percentage"] = (
            (direct_match_sum / total_with_match) if total_with_match > 0 else None
        )
    else:
        metrics["direct_match_percentage"] = None

    return metrics


def parse_sarif_file(sarif_data: dict) -> dict:
    file_findings = {}
    rule_to_cwe_map = {}

    if not sarif_data.get("runs"):
        print("Warning: SARIF file has no 'runs' array.")
        return {}

    run = sarif_data["runs"][0]

    driver_rules = run.get("tool", {}).get("driver", {}).get("rules", [])
    for rule in driver_rules:
        rule_id = rule.get("id")
        cwe_list = rule.get("properties", {}).get("cwe", [])
        if rule_id and cwe_list:
            rule_to_cwe_map[rule_id] = cwe_list

    results = run.get("results", [])
    for result in results:
        rule_id = result.get("ruleId")
        if not rule_id or rule_id not in rule_to_cwe_map:
            continue

        cwes_for_rule = rule_to_cwe_map[rule_id]

        locations = result.get("locations", [])
        for loc in locations:
            phys_loc = loc.get("physicalLocation")
            if phys_loc:
                file_path = phys_loc.get("artifactLocation", {}).get("uri")
                if file_path:
                    if file_path not in file_findings:
                        file_findings[file_path] = set()
                    file_findings[file_path].update(cwes_for_rule)

    final_file_findings = {
        file: sorted(list(cwes)) for file, cwes in file_findings.items()
    }

    return final_file_findings


def evaluate_scans(
    input_folder: Path,
    output_folder: Path,
    expected_results_path: Path,
    count_close_matches: bool,
):
    try:
        df_expected = pd.read_csv(expected_results_path)
        df_expected["is_real_vulnerability"] = (
            df_expected["is_real_vulnerability"].astype(str).str.lower() == "true"
        )
        print(f"Loaded {len(df_expected)} entries from expected results file.")
    except FileNotFoundError:
        print(f"Error: Expected results file not found at '{expected_results_path}'")
        return
    except Exception as e:
        print(f"Error loading expected results file: {e}")
        return

    scan_files = list(input_folder.glob("*.sarif"))
    if not scan_files:
        print(f"Error: No SARIF files found in the input folder '{input_folder}'")
        return

    output_folder.mkdir(parents=True, exist_ok=True)
    print(f"\nFound {len(scan_files)} scan file(s) to process...")

    summary_data = []  # List to hold all summary rows for the final summary file

    for input_file_path in scan_files:
        print(f"\n--- Processing: {input_file_path.name} ---")
        try:
            with open(input_file_path, "r", encoding="utf-8") as f:
                sarif_data = json.load(f)
            scan_results = parse_sarif_file(sarif_data)
        except Exception as e:
            print(f"Error processing file {input_file_path.name}: {e}. Skipping.")
            continue

        results_for_file = []
        for _, expected_row in df_expected.iterrows():
            file_name = expected_row["file_name"]
            is_vulnerable = expected_row["is_real_vulnerability"]
            expected_cwe = expected_row["cwe"]

            found_cwes_list = scan_results.get(file_name, [])
            found_cwes_str = ",".join(found_cwes_list) if found_cwes_list else ""

            acceptable_cwes = (
                CWE_HIERARCHY.get(expected_cwe, [expected_cwe])
                if count_close_matches
                else [expected_cwe]
            )

            cwe_found = False
            matched_cwe = ""
            if expected_cwe in found_cwes_list:
                cwe_found = True
                matched_cwe = expected_cwe
            else:
                for cwe in found_cwes_list:
                    if cwe in acceptable_cwes:
                        cwe_found = True
                        matched_cwe = cwe
                        break

            tp, fp, tn, fn = 0, 0, 0, 0
            if cwe_found and is_vulnerable:
                tp = 1
            elif cwe_found and not is_vulnerable:
                fp = 1
            elif not cwe_found and not is_vulnerable:
                tn = 1
            elif not cwe_found and is_vulnerable:
                fn = 1

            output_row = {
                "file_name": file_name,
                "is_real_vulnerability": int(is_vulnerable),
                "correct_cwe": expected_cwe,
                "matched_cwe": matched_cwe,
                "direct_match": int(matched_cwe == expected_cwe) if matched_cwe else "",
                "tp": tp,
                "fp": fp,
                "tn": tn,
                "fn": fn,
                "found_cwes": found_cwes_str,
            }
            results_for_file.append(output_row)

        if not results_for_file:
            print(f"No entries processed for {input_file_path.name}.")
            continue

        output_df = pd.DataFrame(results_for_file)
        output_columns = [
            "file_name",
            "is_real_vulnerability",
            "correct_cwe",
            "matched_cwe",
            "direct_match",
            "tp",
            "fp",
            "tn",
            "fn",
            "found_cwes",
        ]
        output_df = output_df[output_columns]

        output_filename_base = f"{input_file_path.stem}_results"
        if count_close_matches:
            output_filename_base += "_close_matches"
        output_filename = f"{output_filename_base}.csv"
        output_path = output_folder / output_filename

        output_df.to_csv(output_path, index=False)
        print(f"Processed {len(results_for_file)} entries based on ground truth.")
        print(f"Results saved to: {output_path}")

        scan_name = input_file_path.stem
        unique_cwes = output_df["correct_cwe"].unique()

        for cwe in unique_cwes:
            df_cwe_subset = output_df[output_df["correct_cwe"] == cwe]
            metrics = calculate_metrics(df_cwe_subset)
            summary_row = {"scan": scan_name, "cwe": cwe, **metrics}
            summary_data.append(summary_row)

        all_metrics = calculate_metrics(output_df)
        summary_row_all = {"scan": scan_name, "cwe": "all", **all_metrics}
        summary_data.append(summary_row_all)

    if summary_data:
        summary_df = pd.DataFrame(summary_data)
        summary_columns = [
            "scan",
            "cwe",
            "direct_match_percentage",
            "tp",
            "fp",
            "tn",
            "fn",
            "accuracy",
            "precision",
            "recall",
            "f1",
        ]
        summary_df = summary_df[summary_columns]

        timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        summary_filename = f"summary_sarif_{timestamp}.csv"
        summary_output_path = output_folder / summary_filename

        summary_df.to_csv(summary_output_path, index=False, float_format="%.4f")
        print(f"\n--- Summary file created ---")
        print(f"Aggregated summary saved to: {summary_output_path}")
    else:
        print("\n--- No data processed, summary file not created. ---")

    print("\n--- All files processed. ---")


def main():
    parser = argparse.ArgumentParser(
        description="Evaluate SAST scan results from SARIF files against a ground truth file.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "input_folder",
        type=Path,
        help="Folder containing one or more SAST scan result SARIF files.",
    )
    parser.add_argument(
        "output_folder",
        type=Path,
        help="Folder where the output evaluation CSV files will be saved.",
    )
    parser.add_argument(
        "expected_results",
        type=Path,
        help="Path to the ground truth CSV file.",
    )
    parser.add_argument(
        "--count-close-matches",
        action="store_true",
        help="If set, count close CWE matches (defined in CWE_HIERARCHY) as correct.",
    )

    args = parser.parse_args()

    evaluate_scans(
        input_folder=args.input_folder,
        output_folder=args.output_folder,
        expected_results_path=args.expected_results,
        count_close_matches=args.count_close_matches,
    )


if __name__ == "__main__":
    main()
