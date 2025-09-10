import argparse
import os
import csv
import logging
import re
import time
import json
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed
from dotenv import load_dotenv

import vertexai
from vertexai.generative_models import GenerativeModel, GenerationConfig
from google.oauth2 import service_account
from openai import AzureOpenAI
from azure.ai.inference import ChatCompletionsClient
from azure.ai.inference.models import UserMessage
from azure.core.credentials import AzureKeyCredential

load_dotenv()

MODEL_CONFIG = {
    # GCP Vertex AI Models
    "gemini-2.5-pro": {
        "provider": "vertex_ai",
        "pricing": {
            "input": 1.25,
            "output": 10,
        },
    },
    "gemini-2.5-flash": {
        "provider": "vertex_ai",
        "pricing": {
            "input": 0.3,
            "output": 2.5,
        },
    },
    # Azure OpenAI Models
    "gpt-4o": {
        "provider": "azure_openai",
        "pricing": {"input": 2.5, "output": 10},
        "temperature": 0,
        "can_reason": False,
    },
    "gpt-4.1-mini": {
        "provider": "azure_openai",
        "pricing": {"input": 0.4, "output": 1.6},
        "temperature": 0,
        "can_reason": False,
    },
    "gpt-4.1": {
        "provider": "azure_openai",
        "pricing": {"input": 2, "output": 8},
        "temperature": 0,
        "can_reason": False,
    },
    "o3": {
        "provider": "azure_openai",
        "pricing": {"input": 2, "output": 8},
        "temperature": 1,
        "can_reason": True,
        "reasoning_effort": "medium",
    },
    "o4-mini": {
        "provider": "azure_openai",
        "pricing": {"input": 1.1, "output": 4.4},
        "temperature": 1,
        "can_reason": True,
        "reasoning_effort": "medium",
    },
    "gpt-5": {
        "provider": "azure_openai",
        "pricing": {"input": 1.25, "output": 10},
        "temperature": 1,
        "can_reason": True,
        "reasoning_effort": "medium",
    },
    "gpt-5-mini": {
        "provider": "azure_openai",
        "pricing": {"input": 0.25, "output": 2},
        "temperature": 1,
        "can_reason": True,
        "reasoning_effort": "medium",
    },
    "gpt-5-nano": {
        "provider": "azure_openai",
        "pricing": {"input": 0.05, "output": 0.4},
        "temperature": 1,
        "can_reason": True,
        "reasoning_effort": "medium",
    },
    "gpt-5-chat": {
        "provider": "azure_openai",
        "pricing": {"input": 1.25, "output": 10},
        "temperature": 0,
        "can_reason": True,
        "reasoning_effort": "medium",
    },
    # Azure AI Inference Models
    "DeepSeek-R1-0528": {
        "provider": "azure_inference",
        "pricing": {"input": 1.35, "output": 5.4},
        "temperature": 0,
        "can_reason": True,
    },
    "DeepSeek-V3-0324": {
        "provider": "azure_inference",
        "pricing": {"input": 1.485, "output": 4.56},
        "temperature": 0,
        "can_reason": False,
    },
    "mistral-medium-2505": {
        "provider": "azure_inference",
        "pricing": {"input": 0.4, "output": 2},
        "temperature": 0,
        "can_reason": False,
    },
}

PROMPT_TEMPLATE_ZERO_SHOT = """
You are a security expert specializing in identifying vulnerabilities in source code.
Analyze the following source code and identify any potential security vulnerabilities and categorize them by their corresponding CWE.
Important: Respond only with a comma-separated list of CWE identifiers (e.g., "CWE-89,CWE-79") if vulnerabilities are found, or "Not vulnerable" if no vulnerabilities are found.
Source code to be analyzed:
```
{file_content}
```
"""

PROMPT_TEMPLATE_FEW_SHOT = """
You are a security expert specializing in identifying vulnerabilities in source code.
Analyze the following source code and identify any potential security vulnerabilities and categorize them by their corresponding CWE.
Important: Respond only with a comma-separated list of CWE identifiers (e.g., "CWE-89,CWE-79") if vulnerabilities are found, or "Not vulnerable" if no vulnerabilities are found.

Here are some examples:
Example 1:
Input:
public void login(Connection conn, String username, String password) throws SQLException, NoSuchAlgorithmException {
    String sql = "SELECT * FROM users WHERE username = '" + username + "'";
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(password.getBytes());
    byte[] passwordHash = md.digest();
}

Output:
CWE-89,CWE-328

Example 2:
Input:
public String readFile(String filename) throws IOException {
    String baseDir = "/var/user_files/";
    Path filePath = Paths.get(baseDir + filename);
    return new String(Files.readAllBytes(filePath));
}

Output:
CWE-22

Example 3:
Input:
public boolean getUser(Connection conn, String username) throws SQLException {
    String sql = "SELECT user_id FROM users WHERE username = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        return rs.next(); // Returns true if a record was found.
    }
}

Output:
Not vulnerable

Source code to be analyzed:
Input:
```
{file_content}
```
Output:
"""

PROMPT_TEMPLATE_COT = """
You are a security expert specializing in identifying vulnerabilities in source code.
Analyze the following source code and identify any potential security vulnerabilities and categorize them by their corresponding CWE.
Source code to be analyzed:
```
{file_content}
```
Let's think step by step.
Important: The last line of your response should be a comma-separated list of CWE identifiers (e.g., "CWE-89,CWE-79") if vulnerabilities are found, or "Not vulnerable" if no vulnerabilities are found.
"""

PROMPT_TEMPLATES = {
    "zero_shot": PROMPT_TEMPLATE_ZERO_SHOT,
    "few_shot": PROMPT_TEMPLATE_FEW_SHOT,
    "cot": PROMPT_TEMPLATE_COT,
}

CSV_HEADER = [
    "file_name",
    "analysis_pass",
    "cwes",
    "input_token",
    "input_cost",
    "reasoning_token",
    "reasoning_cost",
    "output_token",
    "output_cost",
    "total_cost",
    "analysis_time",
]


# --- Logging Setup ---
def setup_logging(log_file_path):
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s - %(levelname)s - %(message)s",
        handlers=[logging.FileHandler(log_file_path), logging.StreamHandler()],
    )


# --- LLM Query Functions ---


def _query_vertex_ai(model_name: str, prompt: str):
    """Queries a GCP Vertex AI (Gemini) model."""
    model = GenerativeModel(model_name)
    generation_config = GenerationConfig(
        temperature=0, max_output_tokens=10000, seed=0, top_p=0.1
    )

    response = model.generate_content(prompt, generation_config=generation_config)

    try:
        text_response = response.candidates[0].content.parts[0].text
    except Exception as e:
        logging.error(response)
        # rethrow the exception after logging
        raise e

    return {
        "text": text_response,
        "input_tokens": response.usage_metadata.prompt_token_count,
        "reasoning_tokens": response.usage_metadata.thoughts_token_count,
        "output_tokens": response.usage_metadata.candidates_token_count,
    }


def _query_azure_openai(model_name: str, prompt: str):
    """Queries an Azure OpenAI model."""
    azure_endpoint = os.getenv("AZURE_OPENAI_ENDPOINT")
    api_key = os.getenv("AZURE_API_KEY")
    if model_name == "gpt-4.1":  # different region for gpt-4.1
        azure_endpoint = os.getenv("AZURE_OPENAI_ENDPOINT_FC")
        api_key = os.getenv("AZURE_API_KEY_FC")

    client = AzureOpenAI(
        azure_endpoint=azure_endpoint,
        api_key=api_key,
        api_version="2025-01-01-preview",
    )

    params = {
        "model": model_name,
        "messages": [{"role": "user", "content": [{"type": "text", "text": prompt}]}],
        "max_completion_tokens": 10000,
        "temperature": MODEL_CONFIG[model_name]["temperature"],
    }

    if MODEL_CONFIG[model_name]["can_reason"]:
        params["reasoning_effort"] = MODEL_CONFIG[model_name]["reasoning_effort"]
    else:
        params["top_p"] = 0.1

    response = client.chat.completions.create(**params)

    try:
        text_response = response.choices[0].message.content
        if (
            response.choices[0].finish_reason == "length"
        ):  # log if response was cut off due to length
            logging.error(response)
    except Exception as e:
        logging.error(response)
        raise e

    return {
        "text": text_response,
        "input_tokens": response.usage.prompt_tokens,
        "reasoning_tokens": response.usage.completion_tokens_details.reasoning_tokens,
        "output_tokens": response.usage.completion_tokens
        - response.usage.completion_tokens_details.reasoning_tokens,
    }


def _query_azure_inference(model_name: str, prompt: str):
    """Queries an Azure AI Foundry model via the inference SDK."""
    client = ChatCompletionsClient(
        endpoint=os.getenv("AZURE_INFERENCE_ENDPOINT"),
        credential=AzureKeyCredential(os.getenv("AZURE_API_KEY")),
    )

    params = {
        "model": model_name,
        "messages": [UserMessage(content=prompt)],
        "temperature": 0,
        "max_tokens": 10000,
        "seed": 0,
    }

    if model_name != "mistral-medium-2505":
        params["top_p"] = 0.1

    response = client.complete(**params)

    try:
        text_response = response.choices[0].message.content
        if (
            response.choices[0].finish_reason == "length"
        ):  # log if response was cut off due to length
            logging.error(response)
    except Exception as e:
        logging.error(response)
        raise e

    return {
        "text": text_response,
        "input_tokens": response.usage.prompt_tokens,
        "reasoning_tokens": 0,
        "output_tokens": response.usage.completion_tokens,
    }


def query_llm(model_name: str, prompt: str):
    """
    Dispatcher function to query the appropriate LLM based on configuration.
    """
    if model_name not in MODEL_CONFIG:
        raise ValueError(f"Model '{model_name}' not found in MODEL_CONFIG.")

    config = MODEL_CONFIG[model_name]
    provider = config["provider"]

    logging.info(f"Querying model '{model_name}' via provider '{provider}'...")

    if provider == "vertex_ai":
        return _query_vertex_ai(model_name, prompt)
    elif provider == "azure_openai":
        return _query_azure_openai(model_name, prompt)
    elif provider == "azure_inference":
        return _query_azure_inference(model_name, prompt)
    else:
        raise NotImplementedError(f"Provider '{provider}' is not implemented.")


# --- Processing Logic ---


def parse_llm_output(raw_output: str, prompt_type: str):
    raw_output = raw_output.strip()
    final_answer = raw_output

    lines = raw_output.split("\n")
    non_empty_lines = [line.strip() for line in lines if line.strip()]
    if not non_empty_lines:
        return None  # No output

    final_answer = non_empty_lines[-1]

    # Regex to validate the format: "CWE-..." list or "Not vulnerable"
    cwe_pattern = r"^CWE-\d+(,CWE-\d+)*$"
    pattern = re.compile(rf"^(Not vulnerable|{cwe_pattern})$", re.IGNORECASE)

    if pattern.match(final_answer):
        return final_answer
    else:
        return None


def process_file(file_path: str, model_name: str, prompt_type: str):
    """
    Processes a single source code file: reads, prompts, parses, and calculates costs.
    """
    file_name = os.path.basename(file_path)
    logging.info(f"Processing file: {file_name}")

    result = {key: None for key in CSV_HEADER}
    result.update({"file_name": file_name, "analysis_pass": 0})

    try:
        with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
            source_code = f.read()

        prompt_template = PROMPT_TEMPLATES[prompt_type]
        prompt = prompt_template.format(file_content=source_code)

        start_time = time.time()
        llm_response = query_llm(model_name, prompt)
        analysis_time = time.time() - start_time

        raw_output = llm_response.get("text", "")
        parsed_cwes = parse_llm_output(raw_output, prompt_type)

        # Token and Cost Calculation
        pricing = MODEL_CONFIG[model_name]["pricing"]
        input_tokens = llm_response.get("input_tokens", 0)
        reasoning_tokens = llm_response.get("reasoning_tokens", 0)
        output_tokens = llm_response.get("output_tokens", 0)

        input_cost = input_tokens * pricing["input"] / 1000000
        reasoning_cost = reasoning_tokens * pricing["output"] / 1000000
        output_cost = output_tokens * pricing["output"] / 1000000
        total_cost = input_cost + reasoning_cost + output_cost

        result.update(
            {
                "input_token": input_tokens,
                "input_cost": input_cost,
                "reasoning_token": reasoning_tokens,
                "reasoning_cost": reasoning_cost,
                "output_token": output_tokens,
                "output_cost": output_cost,
                "total_cost": total_cost,
                "analysis_time": analysis_time,
            }
        )

        if parsed_cwes is None:
            logging.warning(
                f"Output validation failed for {file_name}. Model output:\n---\n{raw_output}\n---"
            )
            return result

        result.update({"analysis_pass": 1, "cwes": parsed_cwes})
        logging.info(f"Successfully processed {file_name}.")
        return result

    except Exception as e:
        logging.error(
            f"An unexpected error occurred while processing {file_name}: {e}",
            exc_info=True,
        )
        return result


# --- Main Execution ---
def main():
    parser = argparse.ArgumentParser(
        description="Evaluate LLMs for source code vulnerability scanning.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "input_folder", help="Path to the folder containing source code files."
    )
    parser.add_argument(
        "output_folder", help="Path to the folder where results and logs will be saved."
    )
    parser.add_argument(
        "model_name", choices=MODEL_CONFIG.keys(), help="Name/ID of the LLM to be used."
    )
    parser.add_argument(
        "prompt_type",
        choices=PROMPT_TEMPLATES.keys(),
        help="The type of prompt to use.",
    )
    parser.add_argument(
        "-p",
        "--parallel_jobs",
        type=int,
        default=4,
        help="Number of files to process in parallel.",
    )

    args = parser.parse_args()

    if MODEL_CONFIG[args.model_name]["provider"] == "vertex_ai":
        try:
            service_account_info = json.loads(os.getenv("SERVICE_ACCOUNT_JSON"))
            project_id = service_account_info.get("project_id")
            gcp_location = os.getenv("GCP_LOCATION")
            credentials = service_account.Credentials.from_service_account_info(
                service_account_info
            )
            vertexai.init(
                project=project_id, location=gcp_location, credentials=credentials
            )
        except Exception as e:
            logging.error(f"Failed to initialize Vertex AI: {e}")
            return

    os.makedirs(args.output_folder, exist_ok=True)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_file_path = os.path.join(
        args.output_folder, f"{args.model_name}_{args.prompt_type}_{timestamp}.log"
    )
    setup_logging(log_file_path)

    logging.info("--- Starting LLM Vulnerability Scan ---")
    logging.info(f"Arguments: {vars(args)}")

    try:
        files_to_process = [
            os.path.join(args.input_folder, f)
            for f in os.listdir(args.input_folder)
            if os.path.isfile(os.path.join(args.input_folder, f))
        ]
        if not files_to_process:
            logging.warning(f"No files found in input folder: {args.input_folder}")
            return
    except FileNotFoundError:
        logging.error(f"Input folder not found: {args.input_folder}")
        return

    csv_file_name = f"{args.model_name}_{args.prompt_type}_{timestamp}.csv"
    csv_file_path = os.path.join(args.output_folder, csv_file_name)

    executor = ThreadPoolExecutor(max_workers=args.parallel_jobs)

    try:
        with open(csv_file_path, "w", newline="", encoding="utf-8") as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=CSV_HEADER)
            writer.writeheader()

            logging.info(
                f"Found {len(files_to_process)} files to process. Starting parallel execution..."
            )

            future_to_file = {
                executor.submit(process_file, fp, args.model_name, args.prompt_type): fp
                for fp in files_to_process
            }

            for future in as_completed(future_to_file):
                try:
                    result = future.result()
                    if result:
                        writer.writerow(result)
                except Exception as e:
                    file_path = future_to_file[future]
                    logging.error(
                        f"A critical error occurred in the thread for {os.path.basename(file_path)}: {e}"
                    )
                    writer.writerow(
                        {"file_name": os.path.basename(file_path), "analysis_pass": 0}
                    )

    except KeyboardInterrupt:
        logging.warning(
            "Shutdown signal (Ctrl+C) received. Cancelling pending tasks..."
        )
        executor.shutdown(wait=False, cancel_futures=True)
        logging.info("Executor has been shut down.")

    logging.info("--- Scan Complete or Abort ---")
    logging.info(f"Results saved to: {csv_file_path}")
    logging.info(f"Logs saved to: {log_file_path}")


if __name__ == "__main__":
    main()
