import os
import json
import argparse
import concurrent.futures
import threading
from pathlib import Path
from dotenv import load_dotenv

import vertexai
from vertexai.generative_models import GenerativeModel, GenerationConfig
from google.oauth2 import service_account

MODEL_PRICING = {
    "gemini-2.5-pro": {
        "input": 0.00125,
        "output": 0.01,
    },
    "gemini-2.5-flash": {
        "input": 0.0003,
        "output": 0.0025,
    },
}

PROMPT_TEMPLATE = """
Adapt the following Java file following the listed rules:
- Remove all comments
- Don't change the code formatting or structure itself
1:1 replace the following values
- org.owasp -> org.test
- org.owasp.esapi.ESAPI -> org.test.samplelib.SAMPLEFUNC
- org.owasp.benchmark-> org.test.testlib
- BenchmarkTest* -> SampleValue (number should be omitted)
- "/*/*" (Example: "/pathtraver-00/BenchmarkTest00001", "/pathtraver-00/BenchmarkTest00001.html") -> "/sample" or "/sample.html" (take original file extension)
- Following values used as strings: cmdi, crypto, hash, ldpi, pathtraver, securecookie, sqli, trustbound, weakrand, xpathi, xss -> sample

Only output the contents of the changed file, don't use Markdown. Don't add a description on why you changed something. Your output will directly be used to create new .java files, so it is very important that you don't add anything extra. Important: Do not add ```java ``` around the code! As I said your output will be used in a subsequent script!

Here is the file:
```java
{file_content}
```
"""

def process_file(
    input_file_path: Path,
    output_file_path: Path,
    model: GenerativeModel,
    generation_config: GenerationConfig,
    pricing_info: dict,
    total_stats: dict,
    usage_lock: threading.Lock
):
    print(f"[INFO] Starting to process: {input_file_path.name}")
    try:
        file_content = input_file_path.read_text(encoding='utf-8')
        prompt = PROMPT_TEMPLATE.format(file_content=file_content)

        response = model.generate_content(
            prompt,
            generation_config=generation_config
        )

        # --- Token and Cost Calculation ---
        input_tokens = 0
        reasoning_tokens = 0
        output_tokens = 0
        file_cost = 0.0

        if hasattr(response, 'usage_metadata'):
            input_tokens = response.usage_metadata.prompt_token_count
            output_tokens = response.usage_metadata.candidates_token_count
            reasoning_tokens = response.usage_metadata.thoughts_token_count
            
            if pricing_info:
                # Calculate cost for this specific file
                input_cost = (input_tokens / 1000) * pricing_info.get("input", 0)
                reasoning_cost = (reasoning_tokens / 1000) * pricing_info.get("output", 0)
                output_cost = (output_tokens / 1000) * pricing_info.get("output", 0)
                file_cost = input_cost + reasoning_cost + output_cost

            # Thread-safe update of total statistics
            with usage_lock:
                total_stats['input_tokens'] += input_tokens
                total_stats['reasoning_tokens'] += reasoning_tokens
                total_stats['output_tokens'] += output_tokens
                total_stats['cost'] += file_cost

        # --- Response Handling ---
        if response.candidates and response.candidates[0].content.parts:
            generated_text = response.candidates[0].content.parts[0].text
            output_file_path.write_text(generated_text, encoding='utf-8')
            print(f"[SUCCESS] Finished {input_file_path.name}. "
                  f"Tokens (In: {input_tokens}, Reasoning: {reasoning_tokens}, Out: {output_tokens}). "
                  f"Cost: ${file_cost:.6f}")
        else:
            print(f"[WARNING] No valid content for {input_file_path.name}. Skipped. "
                  f"Reason: {response.candidates[0].finish_reason if response.candidates else 'N/A'}")
            print(f"[DEBUG] Full response for {input_file_path.name}: {response}")

    except Exception as e:
        print(f"[ERROR] An unexpected error occurred while processing file {input_file_path.name}. Reason: {e}")


def main():
    parser = argparse.ArgumentParser(description="Concurrently process files and log API usage cost.")
    parser.add_argument("input_folder", type=str, help="Path to the folder containing input files.")
    parser.add_argument("output_folder", type=str, help="Path to the folder where output files will be saved.")
    parser.add_argument("--parallel", type=int, default=10, help="Number of files to process in parallel (default: 10).")
    args = parser.parse_args()

    print("[INFO] Loading environment variables...")
    load_dotenv()

    service_account_json_str = os.getenv("SERVICE_ACCOUNT_JSON")
    gcp_location = os.getenv("GCP_LOCATION")
    model_name = os.getenv("MODEL_NAME", "gemini-1.5-pro-latest")

    if not all([service_account_json_str, gcp_location]):
        print("[ERROR] SERVICE_ACCOUNT_JSON and GCP_LOCATION must be set in the .env file.")
        return

    # Get pricing for the selected model
    pricing_info = MODEL_PRICING.get(model_name)
    if not pricing_info:
        print(f"[WARNING] Pricing information for model '{model_name}' not found. Cost will not be calculated.")

    try:
        service_account_info = json.loads(service_account_json_str)
        project_id = service_account_info.get("project_id")
        credentials = service_account.Credentials.from_service_account_info(service_account_info)
        
        print(f"[INFO] Initializing Vertex AI for project '{project_id}' in location '{gcp_location}'...")
        vertexai.init(project=project_id, location=gcp_location, credentials=credentials)

        model = GenerativeModel(model_name)
        generation_config = GenerationConfig(temperature=0, max_output_tokens=60000)
        print(f"[INFO] Vertex AI configured successfully with model '{model_name}'.")

    except Exception as e:
        print(f"[ERROR] Failed to configure Vertex AI. Reason: {e}")
        return

    input_path = Path(args.input_folder)
    output_path = Path(args.output_folder)
    output_path.mkdir(parents=True, exist_ok=True)
    files_to_process = [f for f in input_path.iterdir() if f.is_file()]

    if not files_to_process:
        print(f"[INFO] No files found in {input_path}.")
        return

    print(f"[INFO] Found {len(files_to_process)} files to process.")
    print(f"[INFO] Starting processing with {args.parallel} parallel runs...")

    # --- Shared state for concurrent processing ---
    total_stats = {
        'input_tokens': 0,
        'reasoning_tokens': 0,
        'output_tokens': 0,
        'cost': 0.0
    }
    usage_lock = threading.Lock()

    with concurrent.futures.ThreadPoolExecutor(max_workers=args.parallel) as executor:
        future_to_file = {
            executor.submit(process_file, file_path, output_path / file_path.name, model, generation_config, pricing_info, total_stats, usage_lock): file_path
            for file_path in files_to_process
        }

        for future in concurrent.futures.as_completed(future_to_file):
            try:
                future.result()
            except Exception as exc:
                file_path = future_to_file[future]
                print(f"[ERROR] An exception was generated for file {file_path.name}: {exc}")

    # --- Final Summary Report ---
    print("\n" + "="*50)
    print("           API USAGE & COST SUMMARY")
    print("="*50)
    print(f"Total Files Processed: {len(files_to_process)}")
    print(f"Total Input Tokens:     {total_stats['input_tokens']:,}")
    print(f"Total Reasoning Tokens:     {total_stats['reasoning_tokens']:,}")
    print(f"Total Output Tokens:  {total_stats['output_tokens']:,}")
    print("-" * 50)
    print(f"Estimated Total Cost:    ${total_stats['cost']:.6f}")
    print("="*50)


if __name__ == "__main__":
    main()