import pandas as pd
import argparse
from pathlib import Path
from datetime import datetime


def calculate_metrics(df: pd.DataFrame) -> dict:
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


def evaluate_fp_mitigation(input_folder: Path, output_folder: Path):
    """
    Processes false positive mitigation results from CSV files in an input folder
    and generates a single summary report.
    """
    scan_files = list(input_folder.glob("*.csv"))
    if not scan_files:
        print(f"Error: No CSV files found in the input folder '{input_folder}'")
        return

    # Create output directory if it doesn't exist
    output_folder.mkdir(parents=True, exist_ok=True)
    print(f"\nFound {len(scan_files)} result file(s) to process...")

    summary_data = []  # List to hold all summary rows for the final summary file

    for input_file_path in scan_files:
        print(f"\n--- Processing: {input_file_path.name} ---")
        try:
            df_scan = pd.read_csv(input_file_path)
            # Rename the 'new' confusion matrix columns to be used by the metrics calculator
            df_scan.rename(
                columns={
                    "tp_new": "tp",
                    "fp_new": "fp",
                    "tn_new": "tn",
                    "fn_new": "fn",
                },
                inplace=True,
            )
        except Exception as e:
            print(f"Error reading file {input_file_path.name}: {e}. Skipping.")
            continue

        # Extract model and prompting type from filename
        try:
            parts = input_file_path.stem.split("_")
            model = parts[0]
            prompting_type = parts[1]
        except IndexError:
            print(
                f"Warning: Could not parse model and prompting_type from filename '{input_file_path.name}'. Using 'unknown'."
            )
            model, prompting_type = "unknown", "unknown"

        print(f"Aggregating metrics for {len(df_scan)} entries.")

        # Get all unique CWEs from the input file to create per-CWE metrics
        unique_cwes = df_scan["correct_cwe"].unique()

        # Calculate metrics for each specific CWE
        for cwe in unique_cwes:
            if pd.isna(cwe):
                continue
            df_cwe_subset = df_scan[df_scan["correct_cwe"] == cwe]
            metrics = calculate_metrics(df_cwe_subset)
            summary_row = {
                "model": model,
                "prompting_type": prompting_type,
                "cwe": cwe,
                **metrics,
            }
            summary_data.append(summary_row)

        # Calculate metrics for "all" CWEs in the file
        all_metrics = calculate_metrics(df_scan)
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

        # Ensure all columns exist, filling missing ones with None, then reorder
        for col in summary_columns:
            if col not in summary_df.columns:
                summary_df[col] = None
        summary_df = summary_df[summary_columns]

        # Generate timestamp for the summary filename
        timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        summary_filename = f"summary_fp_mitigation_{timestamp}.csv"
        summary_output_path = output_folder / summary_filename

        summary_df.to_csv(summary_output_path, index=False, float_format="%.4f")
        print(f"\n--- Summary file created ---")
        print(f"Aggregated summary saved to: {summary_output_path}")
    else:
        print("\n--- No data processed, summary file not created. ---")

    print("\n--- All files processed. ---")


def main():
    parser = argparse.ArgumentParser(
        description="Evaluate LLM-based false positive mitigation results and generate a summary report.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "input_folder",
        type=Path,
        help="Folder containing one or more false positive mitigation result CSV files.",
    )
    parser.add_argument(
        "output_folder",
        type=Path,
        help="Folder where the output summary CSV will be saved.",
    )

    args = parser.parse_args()

    evaluate_fp_mitigation(
        input_folder=args.input_folder,
        output_folder=args.output_folder,
    )


if __name__ == "__main__":
    main()
