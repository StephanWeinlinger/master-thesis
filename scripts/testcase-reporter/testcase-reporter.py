import pandas as pd
import argparse
import os
from pathlib import Path
from datetime import datetime

# This hierarchy defines which CWEs are considered "close matches" to a given CWE.
CWE_HIERARCHY = {
    "CWE-22": ["CWE-22", "CWE-23", "CWE-36"],  # Path Traversal variations
    "CWE-78": ["CWE-78", "CWE-77"],  # OS Command Injection variations
    "CWE-79": ["CWE-79", "CWE-80", "CWE-74"],  # Cross-Site Scripting (XSS) variations
    "CWE-89": ["CWE-89", "CWE-564", "CWE-943"],  # SQL Injection variations
    "CWE-90": ["CWE-90", "CWE-943"],  # LDAP Injection variations
    "CWE-327": ["CWE-327", "CWE-328"],  # Cryptographic Issues
    "CWE-328": ["CWE-328", "CWE-327", "CWE-326", "CWE-916"],  # Cryptographic Issues
    "CWE-330": ["CWE-330", "CWE-338"],  # Cryptographic Issues
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
    df_matched["direct_match"] = pd.to_numeric(
        df_matched["direct_match"], errors="coerce"
    )

    if not df_matched.empty:
        direct_match_sum = df_matched["direct_match"].sum()
        total_with_match = len(df_matched)
        metrics["direct_match_percentage"] = (
            (direct_match_sum / total_with_match) if total_with_match > 0 else None
        )
    else:
        metrics["direct_match_percentage"] = None

    # Token, cost, and time metrics
    metrics["avg_input_tokens"] = df["input_token"].mean()
    metrics["avg_reasoning_tokens"] = df["reasoning_token"].mean()
    metrics["avg_output_tokens"] = df["output_token"].mean()
    metrics["avg_cost"] = df["total_cost"].mean()
    metrics["total_cost"] = df["total_cost"].sum()
    metrics["avg_time"] = df["analysis_time"].mean()

    # Error count
    metrics["total_errors"] = df["error"].sum()

    return metrics


def evaluate_scans(
    input_folder: Path,
    output_folder: Path,
    expected_results_path: Path,
    count_close_matches: bool,
):
    try:
        df_expected = pd.read_csv(expected_results_path)
        df_expected.set_index("file_name", inplace=True)
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

    scan_files = list(input_folder.glob("*.csv"))
    if not scan_files:
        print(f"Error: No CSV files found in the input folder '{input_folder}'")
        return

    # Create output directory if it doesn't exist
    output_folder.mkdir(parents=True, exist_ok=True)
    print(f"\nFound {len(scan_files)} scan file(s) to process...")

    summary_data = []  # List to hold all summary rows for the final summary file

    for input_file_path in scan_files:
        print(f"\n--- Processing: {input_file_path.name} ---")
        try:
            df_scan = pd.read_csv(input_file_path)
            df_scan["cwes"] = df_scan["cwes"].fillna("")
        except Exception as e:
            print(f"Error reading file {input_file_path.name}: {e}. Skipping.")
            continue

        # Extract model and prompting type from filename
        try:
            parts = input_file_path.name.split("_")
            model = parts[0]
            prompting_type = parts[1]
        except IndexError:
            print(
                f"Warning: Could not parse model and prompting_type from filename '{input_file_path.name}'. Using 'unknown'."
            )
            model, prompting_type = "unknown", "unknown"

        results_for_file = []
        for _, scan_row in df_scan.iterrows():
            file_name = scan_row["file_name"]

            try:
                expected_row = df_expected.loc[file_name]
            except KeyError:
                print(
                    f"Warning: No expected result found for '{file_name}'. Skipping row."
                )
                continue

            is_vulnerable = expected_row["is_real_vulnerability"]
            expected_cwe = expected_row["cwe"]

            found_cwes_str = scan_row["cwes"]
            found_cwes_list = [
                cwe.strip() for cwe in found_cwes_str.split(",") if cwe.strip()
            ]
            found_cwes_list = list(set(found_cwes_list))

            acceptable_cwes = (
                CWE_HIERARCHY.get(expected_cwe, [expected_cwe])
                if count_close_matches
                else [expected_cwe]
            )

            cwe_found = False
            matched_cwe = ""
            # Check for direct match first
            if expected_cwe in found_cwes_list:
                cwe_found = True
                matched_cwe = expected_cwe
            else:
                # Find the first acceptable CWE in the list of found CWEs
                for cwe in found_cwes_list:
                    if cwe in acceptable_cwes:
                        cwe_found = True
                        matched_cwe = cwe
                        break

            tp, fp, tn, fn = 0, 0, 0, 0
            if cwe_found and is_vulnerable:
                tp = 1  # True Positive
            elif cwe_found and not is_vulnerable:
                fp = 1  # False Positive
            elif not cwe_found and not is_vulnerable:
                tn = 1  # True Negative
            elif not cwe_found and is_vulnerable:
                fn = 1  # False Negative

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
                **scan_row.drop("file_name").to_dict(),
            }
            results_for_file.append(output_row)

        if not results_for_file:
            print(
                f"No processable entries found in {input_file_path.name}. No output file will be generated."
            )
            continue

        output_df = pd.DataFrame(results_for_file)

        output_columns = [
            "file_name",
            "is_real_vulnerability",
            "error",
            "correct_cwe",
            "matched_cwe",
            "direct_match",
            "tp",
            "fp",
            "tn",
            "fn",
            "found_cwes",
            "input_token",
            "input_cost",
            "reasoning_token",
            "reasoning_cost",
            "output_token",
            "output_cost",
            "total_cost",
            "analysis_time",
        ]
        for col in output_columns:
            if col not in output_df.columns:
                output_df[col] = None
        output_df = output_df[output_columns]

        output_filename = f"{input_file_path.stem}_results.csv"
        if count_close_matches:
            output_filename = f"{input_file_path.stem}_results_close_matches.csv"
        output_path = output_folder / output_filename

        output_df = output_df.sort_values(by="file_name")
        output_df.to_csv(output_path, index=False)
        print(f"Processed {len(results_for_file)} entries.")
        print(f"Results saved to: {output_path}")

        # Get all unique expected CWEs for this file
        unique_cwes = output_df["correct_cwe"].unique()

        # Calculate metrics for each specific CWE
        for cwe in unique_cwes:
            df_cwe_subset = output_df[output_df["correct_cwe"] == cwe]
            metrics = calculate_metrics(df_cwe_subset)
            summary_row = {
                "model": model,
                "prompting_type": prompting_type,
                "cwe": cwe,
                **metrics,
            }
            summary_data.append(summary_row)

        # Calculate metrics for "all" CWEs in the file
        all_metrics = calculate_metrics(output_df)
        summary_row_all = {
            "model": model,
            "prompting_type": prompting_type,
            "cwe": "all",
            **all_metrics,
        }
        summary_data.append(summary_row_all)

    if summary_data:
        summary_df = pd.DataFrame(summary_data)

        # Define and set column order for the summary file
        summary_columns = [
            "model",
            "prompting_type",
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
            "avg_input_tokens",
            "avg_reasoning_tokens",
            "avg_output_tokens",
            "avg_cost",
            "total_cost",
            "avg_time",
            "total_errors",
        ]
        summary_df = summary_df[summary_columns]

        # Generate timestamp for the summary filename
        timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        summary_filename = f"summary_{timestamp}.csv"
        summary_output_path = output_folder / summary_filename

        summary_df.to_csv(summary_output_path, index=False, float_format="%.4f")
        print(f"\n--- Summary file created ---")
        print(f"Aggregated summary saved to: {summary_output_path}")
    else:
        print("\n--- No data processed, summary file not created. ---")

    print("\n--- All files processed. ---")


def main():
    parser = argparse.ArgumentParser(
        description="Evaluate SAST scan results against a ground truth file, creating a separate output for each input file and a final summary file.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "input_folder",
        type=Path,
        help="Folder containing one or more SAST scan result CSV files.",
    )
    parser.add_argument(
        "output_folder",
        type=Path,
        help="Folder where the output evaluation CSVs will be saved.",
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
