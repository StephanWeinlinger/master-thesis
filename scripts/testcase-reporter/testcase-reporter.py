import pandas as pd
import argparse
import os
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


def evaluate_scans(
    input_folder: Path,
    output_folder: Path,
    expected_results_path: Path,
    count_close_matches: bool,
):
    # 1. Load Expected Results (Ground Truth) - This is done once.
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

    # 2. Find all SAST scan result CSVs and process them one by one
    scan_files = list(input_folder.glob("*.csv"))
    if not scan_files:
        print(f"Error: No CSV files found in the input folder '{input_folder}'")
        return

    # Create output directory if it doesn't exist
    output_folder.mkdir(parents=True, exist_ok=True)
    print(f"\nFound {len(scan_files)} scan file(s) to process...")

    for input_file_path in scan_files:
        print(f"\n--- Processing: {input_file_path.name} ---")
        try:
            df_scan = pd.read_csv(input_file_path)
            df_scan["cwes"] = df_scan["cwes"].fillna("")
        except Exception as e:
            print(f"Error reading file {input_file_path.name}: {e}. Skipping.")
            continue

        # 3. Process each scan result within the current file
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

        # 6. Write results for the current file to its own output CSV
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

        # Construct the new filename
        # Add close matches info to filename if applicable
        output_filename = f"{input_file_path.stem}_results.csv"
        if count_close_matches:
            output_filename = f"{input_file_path.stem}_results_close_matches.csv"
        output_path = output_folder / output_filename

        # Sort output_df by file_name
        output_df = output_df.sort_values(by="file_name")

        output_df.to_csv(output_path, index=False)
        print(f"Processed {len(results_for_file)} entries.")
        print(f"Results saved to: {output_path}")

    print("\n--- All files processed. ---")


def main():
    parser = argparse.ArgumentParser(
        description="Evaluate SAST scan results against a ground truth file, creating a separate output for each input file.",
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
        help="Folder where the output evaluation CSV will be saved.",
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
