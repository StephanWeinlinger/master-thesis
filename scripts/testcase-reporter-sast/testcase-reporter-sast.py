import json
import pandas as pd
import argparse
from pathlib import Path

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
                    # Initialize a set for the file if it's the first time we see it
                    if file_path not in file_findings:
                        file_findings[file_path] = set()
                    file_findings[file_path].update(cwes_for_rule)

    # Convert sets of CWEs to sorted lists for consistent output
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
    # 1. Load Expected Results (Ground Truth) - This is done once.
    try:
        df_expected = pd.read_csv(expected_results_path)
        # Keep the index as is, we will iterate over rows
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

    # 2. Find all SAST scan result SARIF files and process them one by one
    scan_files = list(input_folder.glob("*.sarif"))
    if not scan_files:
        print(f"Error: No SARIF files found in the input folder '{input_folder}'")
        return

    # Create output directory if it doesn't exist
    output_folder.mkdir(parents=True, exist_ok=True)
    print(f"\nFound {len(scan_files)} scan file(s) to process...")

    for input_file_path in scan_files:
        print(f"\n--- Processing: {input_file_path.name} ---")
        try:
            with open(input_file_path, "r", encoding="utf-8") as f:
                sarif_data = json.load(f)
            # Parse the SARIF data to get a dictionary of {file: [CWEs]} for files with findings
            scan_results = parse_sarif_file(sarif_data)
        except json.JSONDecodeError as e:
            print(f"Error decoding JSON from {input_file_path.name}: {e}. Skipping.")
            continue
        except Exception as e:
            print(f"Error processing file {input_file_path.name}: {e}. Skipping.")
            continue

        # 3. Iterate through the ground truth and compare against scan results
        results_for_file = []
        for _, expected_row in df_expected.iterrows():
            file_name = expected_row["file_name"]
            is_vulnerable = expected_row["is_real_vulnerability"]
            expected_cwe = expected_row["cwe"]

            # Get the list of found CWEs for this file from the scan results.
            # If the file was not found in the scan results, this returns an empty list.
            found_cwes_list = scan_results.get(file_name, [])

            if found_cwes_list:
                found_cwes_str = ",".join(found_cwes_list)
            else:
                found_cwes_str = "Not vulnerable"

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

            # 4. Classify as TP, FP, TN, FN
            tp, fp, tn, fn = 0, 0, 0, 0
            if cwe_found and is_vulnerable:
                tp = 1  # True Positive
            elif cwe_found and not is_vulnerable:
                fp = 1  # False Positive
            elif not cwe_found and not is_vulnerable:
                tn = 1  # True Negative
            elif not cwe_found and is_vulnerable:
                fn = 1  # False Negative

            # 5. Construct the output row
            output_row = {
                "file_name": file_name,
                "is_real_vulnerability": int(is_vulnerable),
                "correct_cwe": expected_cwe,
                "matched_cwe": matched_cwe,
                "direct_match": int(matched_cwe == expected_cwe) if matched_cwe else 0,
                "tp": tp,
                "fp": fp,
                "tn": tn,
                "fn": fn,
                "found_cwes": found_cwes_str,
            }
            results_for_file.append(output_row)

        if not results_for_file:
            print(
                f"No entries from the expected results could be processed for {input_file_path.name}."
            )
            continue

        # 6. Write results for the current file to its own output CSV
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

        # Construct the new filename
        output_filename_base = f"{input_file_path.stem}_results"
        if count_close_matches:
            output_filename_base += "_close_matches"
        output_filename = f"{output_filename_base}.csv"

        output_path = output_folder / output_filename

        output_df.to_csv(output_path, index=False)
        print(f"Processed {len(results_for_file)} entries based on ground truth.")
        print(f"Results saved to: {output_path}")

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
