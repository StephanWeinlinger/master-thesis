import argparse
import os
import csv
import logging
import json
import time
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
    # Azure AI Inference Models
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
You are a security expert specializing in SAST-Scan result analysis. Your job is to check whether a specific finding is valid (true positive) or not (false positive).
You are given the following information as input:
- Contents of the analyzed source code file
- Output of a SAST-Scan tool that describes a potential vulnerability in the source code file
Analyze the source code and the SAST-Scan output and respond with either "Vulnerable" or "Not vulnerable". Ignore any other vulnerabilities that might exist in the code, only focus on validating the given one. Err on on the side of caution if unsure. The cost of a false negative is much higher than that of a false positive.
Important: Respond only with "Vulnerable" or "Not vulnerable"
Source code of file that was analyzed:
```
{file_content}
```

SAST-Scan output:
```
Title: {scan_title}
Details for specific finding: {scan_details}
Extra information: {scan_extra_info}
```
"""

PROMPT_TEMPLATE_FEW_SHOT = """
You are a security expert specializing in SAST-Scan result analysis. Your job is to check whether a specific finding is valid (true positive) or not (false positive).
You are given the following information as input:
- Contents of the analyzed source code file
- Output of a SAST-Scan tool that describes a potential vulnerability in the source code file
Analyze the source code and the SAST-Scan output and respond with either "Vulnerable" or "Not vulnerable". Ignore any other vulnerabilities that might exist in the code, only focus on validating the given one. Err on on the side of caution if unsure. The cost of a false negative is much higher than that of a false positive.
Important: Respond only with "Vulnerable" or "Not vulnerable"

Here are some examples:
Example 1 Start:

[Input]:
Source code file that was analyzed:
public void login(Connection conn, String username, String password) throws SQLException, NoSuchAlgorithmException {{
    String sql = "SELECT * FROM users WHERE username = '" + username + "'";
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
}}

SAST-Scan output:
Title: SQL Injection
Details for specific finding: Unsanitized input from an HTTP header flows into executeQuery, where it is used in an SQL query. This may result in an SQL Injection vulnerability.
Extra information: ## Details\n\nIn an SQL injection attack, the user can submit an SQL query directly to the database, gaining access without providing appropriate credentials. Attackers can then view, export, modify, and delete confidential information; change passwords and other authentication information; and possibly gain access to other systems within the network. This is one of the most commonly exploited categories of vulnerability, but can largely be avoided through good coding practices.\n\n## Best practices for prevention\n* Avoid passing user-entered parameters directly to the SQL server.\n* Avoid using string concatenation to build SQL queries from user-entered parameters.\n* When coding, define SQL code first, then pass in parameters. Use prepared statements with parameterized queries. Examples include `SqlCommand()` in .NET and `bindParam()` in PHP.\n* Use strong typing for all parameters so unexpected user data will be rejected.\n* Where direct user input cannot be avoided for performance reasons, validate input against a very strict allowlist of permitted characters, avoiding special characters such as `? & / < > ; - ' \" \\` and spaces. Use a vendor-supplied escaping routine if possible.\n* Develop your application in an environment and/or using libraries that provide protection against SQL injection.\n* Harden your entire environment around a least-privilege model, ideally with isolated accounts with privileges only for particular tasks.

[Output]:
Vulnerable

Example 1 End

Example 2 Start:
Input:
public String query(String bar) throws IOException {{
    String test = bar;
    test = "searchString";
    InputSource source = new InputSource(new StringReader(inputXml));
    XPath xpath = XPathFactory.newInstance().newXPath();
    return (String) xpath.evaluate(test, source, XPathConstants.STRING);
}}

SAST-Scan output:
Title: XPath Injection
Details for specific finding: Unsanitized input from cookies flows into compile, where it is used in an XPath query. This may result in an XPath Injection vulnerability.
Extra information: \n## Details\n\nXPath expressions are a standard method of querying data stored in XML format, allowing applications to return data based on attributes, patterns, and more. However, as with other injection-type attacks, if a malicious user is able to manipulate the query that is sent to the server, they may be able to gain control of application flow and logic or access unauthorized data, including user authentication data. This weakness is an inherent design flaw that can be easily corrected through awareness of the hazards of allowing unchecked user input in XPath queries.\n\n## Best practices for prevention\n* Use a parameterized XPath interface for all types of user input if possible.\n* Avoid passing unsanitized parameters to the XML database.\n* Never pass a query directly from the user to the server without sanitizing input.\n* Use a denylist to eliminate user-entered forbidden query characters.\n* Consider using precompiled XPath queries to circumvent issues with user-entered parameters altogether.

[Output]:
Not vulnerable

Example 2 End
Example 3 Start:
[Input]:
public boolean getUser(Connection conn, String username) throws SQLException {{
    String sql = "SELECT user_id FROM users WHERE username = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {{
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        return rs.next(); // Returns true if a record was found.
    }}
}}

SAST-Scan output:
Title: SQL Injection
Details for specific finding: Unsanitized input from an HTTP header flows into executeQuery, where it is used in an SQL query. This may result in an SQL Injection vulnerability.
Extra information: ## Details\n\nIn an SQL injection attack, the user can submit an SQL query directly to the database, gaining access without providing appropriate credentials. Attackers can then view, export, modify, and delete confidential information; change passwords and other authentication information; and possibly gain access to other systems within the network. This is one of the most commonly exploited categories of vulnerability, but can largely be avoided through good coding practices.\n\n## Best practices for prevention\n* Avoid passing user-entered parameters directly to the SQL server.\n* Avoid using string concatenation to build SQL queries from user-entered parameters.\n* When coding, define SQL code first, then pass in parameters. Use prepared statements with parameterized queries. Examples include `SqlCommand()` in .NET and `bindParam()` in PHP.\n* Use strong typing for all parameters so unexpected user data will be rejected.\n* Where direct user input cannot be avoided for performance reasons, validate input against a very strict allowlist of permitted characters, avoiding special characters such as `? & / < > ; - ' \" \\` and spaces. Use a vendor-supplied escaping routine if possible.\n* Develop your application in an environment and/or using libraries that provide protection against SQL injection.\n* Harden your entire environment around a least-privilege model, ideally with isolated accounts with privileges only for particular tasks.

[Output]:
Not vulnerable

Example 3 End

Source code of file that was analyzed:
```
{file_content}
```

SAST-Scan output:
```
Title: {scan_title}
Details for specific finding: {scan_details}
Extra information: {scan_extra_info}
```
"""

PROMPT_TEMPLATE_COT = """
You are a security expert specializing in SAST-Scan result analysis. Your job is to check whether a specific finding is valid (true positive) or not (false positive).
You are given the following information as input:
- Contents of the analyzed source code file
- Output of a SAST-Scan tool that describes a potential vulnerability in the source code file
Analyze the source code and the SAST-Scan output. Ignore any other vulnerabilities that might exist in the code, only focus on validating the given one. Err on on the side of caution if unsure. The cost of a false negative is much higher than that of a false positive.
Source code of file that was analyzed:
```
{file_content}
```

SAST-Scan output:
```
Title: {scan_title}
Details for specific finding: {scan_details}
Extra information: {scan_extra_info}
```
Let's think step by step.
Important: The last line of your response should be either "Vulnerable" or "Not vulnerable". Your response will be parsed automatically, so ensure the final line adheres strictly to this format.
"""

PROMPT_TEMPLATES = {
    "zero-shot": PROMPT_TEMPLATE_ZERO_SHOT,
    "few-shot": PROMPT_TEMPLATE_FEW_SHOT,
    "cot": PROMPT_TEMPLATE_COT,
}

CSV_HEADER = [
    "file_name",
    "error",
    "correct_cwe",
    "matched_cwe",
    "direct_match",
    "was_changed",
    "tp_old",
    "fp_old",
    "tn_old",
    "fn_old",
    "tp_new",
    "fp_new",
    "tn_new",
    "fn_new",
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

    start_time = time.time()
    response = model.generate_content(prompt, generation_config=generation_config)
    analysis_time = time.time() - start_time

    try:
        text_response = response.candidates[0].content.parts[0].text
    except Exception as e:
        logging.error(response)
        raise e

    return {
        "text": text_response,
        "input_tokens": response.usage_metadata.prompt_token_count,
        "reasoning_tokens": response.usage_metadata.thoughts_token_count,
        "output_tokens": response.usage_metadata.candidates_token_count,
    }, analysis_time


def _query_azure_openai(model_name: str, prompt: str):
    """Queries an Azure OpenAI model."""
    azure_endpoint = os.getenv("AZURE_OPENAI_ENDPOINT")
    api_key = os.getenv("AZURE_API_KEY")
    if model_name == "gpt-4.1":
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
        "timeout": 180,
    }

    if MODEL_CONFIG[model_name]["can_reason"]:
        params["reasoning_effort"] = MODEL_CONFIG[model_name]["reasoning_effort"]
    else:
        params["top_p"] = 0.1

    start_time = time.time()
    response = client.chat.completions.create(**params)
    analysis_time = time.time() - start_time

    try:
        text_response = response.choices[0].message.content
        if response.choices[0].finish_reason == "length":
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
    }, analysis_time


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

    start_time = time.time()
    response = client.complete(**params)
    analysis_time = time.time() - start_time

    try:
        text_response = response.choices[0].message.content
        if response.choices[0].finish_reason == "length":
            logging.error(response)
    except Exception as e:
        logging.error(response)
        raise e

    return {
        "text": text_response,
        "input_tokens": response.usage.prompt_tokens,
        "reasoning_tokens": 0,
        "output_tokens": response.usage.completion_tokens,
    }, analysis_time


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


def parse_sarif_file(sarif_path):
    """Parses the SARIF file to create efficient lookup dictionaries."""
    logging.info(f"Parsing SARIF file: {sarif_path}")
    with open(sarif_path, "r", encoding="utf-8") as f:
        sarif_data = json.load(f)

    run = sarif_data["runs"][0]

    rules_by_id = {}
    for rule in run["tool"]["driver"]["rules"]:
        try:
            rule_id = rule["id"]
            rules_by_id[rule_id] = {
                "cwe": rule["properties"]["cwe"][0],
                "scan_title": rule.get("shortDescription", {}).get("text", "N/A"),
                "scan_extra_info": rule.get("help", {}).get("markdown", "N/A"),
            }
        except (KeyError, IndexError) as e:
            logging.warning(
                f"Could not process rule {rule.get('id', 'N/A')}. Missing 'cwe' property or it's empty. Error: {e}"
            )
            continue

    results_by_file = {}
    for result in run["results"]:
        try:
            file_name = result["locations"][0]["physicalLocation"]["artifactLocation"][
                "uri"
            ]
            finding = {
                "rule_id": result["ruleId"],
                "scan_details": result["message"]["text"],
            }
            results_by_file.setdefault(file_name, []).append(finding)
        except (KeyError, IndexError) as e:
            logging.warning(
                f"Could not process a result. Missing expected fields. Error: {e}"
            )
            continue

    logging.info(
        f"Finished parsing SARIF. Found {len(rules_by_id)} rules and results for {len(results_by_file)} files."
    )
    return rules_by_id, results_by_file


def parse_llm_verdict(raw_output: str):
    """Parses the LLM's final line to get 'Vulnerable' (1) or 'Not vulnerable' (0)."""
    if not raw_output:
        return None

    lines = raw_output.strip().split("\n")
    last_line = lines[-1].strip().lower()

    if "not vulnerable" in last_line:
        return 0
    if "vulnerable" in last_line:
        return 1

    return None


def process_finding(
    finding_data, source_folder, model_name, prompt_type, rules_by_id, results_by_file
):
    """
    Processes a single finding: reads source, gets SARIF context, prompts LLM, and evaluates the result.
    """
    file_name = finding_data["file_name"]
    logging.info(f"Processing finding in file: {file_name}")

    result = {
        "file_name": file_name,
        "error": 1,
        "correct_cwe": finding_data.get("correct_cwe"),
        "matched_cwe": finding_data.get("matched_cwe"),
        "direct_match": finding_data.get("direct_match"),
        "was_changed": 0,
        "tp_old": finding_data.get("tp"),
        "fp_old": finding_data.get("fp"),
        "tn_old": finding_data.get("tn"),
        "fn_old": finding_data.get("fn"),
        "tp_new": 0,
        "fp_new": 0,
        "tn_new": 0,
        "fn_new": 0,
        "input_token": 0,
        "input_cost": 0,
        "reasoning_token": 0,
        "reasoning_cost": 0,
        "output_token": 0,
        "output_cost": 0,
        "total_cost": 0,
        "analysis_time": 0,
    }

    try:
        source_file_path = os.path.join(source_folder, file_name)
        with open(source_file_path, "r", encoding="utf-8", errors="ignore") as f:
            file_content = f.read()

        matched_cwe_from_csv = finding_data["matched_cwe"]

        scan_title, scan_details, scan_extra_info = None, None, None

        file_findings = results_by_file.get(file_name, [])
        for finding in file_findings:
            rule_id = finding["rule_id"]
            rule_info = rules_by_id.get(rule_id)
            if rule_info and rule_info["cwe"] == matched_cwe_from_csv:
                scan_title = rule_info["scan_title"]
                scan_extra_info = rule_info["scan_extra_info"]
                scan_details = finding["scan_details"]
                break

        if not all([scan_title, scan_details, scan_extra_info]):
            logging.error(
                f"Could not find a matching SARIF result for file '{file_name}' with CWE '{matched_cwe_from_csv}'."
            )
            return result

        prompt_template = PROMPT_TEMPLATES[prompt_type]
        prompt = prompt_template.format(
            file_content=file_content,
            scan_title=scan_title,
            scan_details=scan_details,
            scan_extra_info=scan_extra_info,
        )

        llm_response, analysis_time = query_llm(model_name, prompt)
        raw_output = llm_response.get("text", "")

        llm_prediction = parse_llm_verdict(raw_output)

        if llm_prediction is None:
            logging.warning(
                f"Output validation failed for {file_name}. Model output:\n---\n{raw_output}\n---"
            )
            return result

        ground_truth = int(finding_data["is_real_vulnerability"])

        if ground_truth == 1 and llm_prediction == 1:
            result["tp_new"] = 1
        elif ground_truth == 0 and llm_prediction == 1:
            result["fp_new"] = 1
        elif ground_truth == 0 and llm_prediction == 0:
            result["tn_new"] = 1
        elif ground_truth == 1 and llm_prediction == 0:
            result["fn_new"] = 1

        if (
            int(result["tp_old"]) != result["tp_new"]
            or int(result["fp_old"]) != result["fp_new"]
        ):
            result["was_changed"] = 1

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
                "error": 0,
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

        logging.info(f"Successfully processed finding in {file_name}.")
        return result

    except FileNotFoundError:
        logging.error(f"Source code file not found: {source_file_path}")
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
        description="Evaluate LLMs for SAST false positive mitigation.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "source_folder", help="Path to the folder containing source code files."
    )
    parser.add_argument(
        "output_folder", help="Path to the folder where results and logs will be saved."
    )
    parser.add_argument(
        "sast_scan_file", help="Path to the input SAST scan file in SARIF format."
    )
    parser.add_argument(
        "evaluation_file",
        help="Path to the input CSV file with ground truth evaluation.",
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
        help="Number of findings to process in parallel.",
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
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    log_file_path = os.path.join(
        args.output_folder, f"{args.model_name}_{args.prompt_type}_{timestamp}.log"
    )
    setup_logging(log_file_path)

    logging.info("--- Starting LLM False Positive Mitigation Evaluation ---")
    logging.info(f"Arguments: {vars(args)}")

    try:
        rules_by_id, results_by_file = parse_sarif_file(args.sast_scan_file)

        with open(args.evaluation_file, "r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            all_findings = list(reader)

        findings_to_process = [
            row for row in all_findings if row.get("tp") == "1" or row.get("fp") == "1"
        ]

        if not findings_to_process:
            logging.warning(
                f"No TP or FP findings found in evaluation file: {args.evaluation_file}"
            )
    except FileNotFoundError as e:
        logging.error(f"Input file not found: {e.filename}")
        return
    except Exception as e:
        logging.error(f"Failed during initial setup: {e}", exc_info=True)
        return

    llm_results = {}
    if findings_to_process:
        executor = ThreadPoolExecutor(max_workers=args.parallel_jobs)
        try:
            logging.info(
                f"Found {len(findings_to_process)} findings to process. Starting parallel execution..."
            )
            future_to_finding = {
                executor.submit(
                    process_finding,
                    finding,
                    args.source_folder,
                    args.model_name,
                    args.prompt_type,
                    rules_by_id,
                    results_by_file,
                ): finding["file_name"]
                for finding in findings_to_process
            }

            for future in as_completed(future_to_finding):
                file_name = future_to_finding[future]
                try:
                    result = future.result()
                    if result:
                        llm_results[file_name] = result
                except Exception as e:
                    logging.error(
                        f"A critical error occurred in the thread for {file_name}: {e}"
                    )
                    llm_results[file_name] = {"file_name": file_name, "error": 1}
        except KeyboardInterrupt:
            logging.warning(
                "Shutdown signal (Ctrl+C) received. Cancelling pending tasks..."
            )
            executor.shutdown(wait=False, cancel_futures=True)
            logging.info("Executor has been shut down.")

    final_output_rows = []
    for row in all_findings:
        file_name = row["file_name"]
        if file_name in llm_results:
            final_output_rows.append(llm_results[file_name])
        else:
            # This is a TN or FN row that was not processed
            unprocessed_row = {
                "file_name": file_name,
                "error": 0,
                "correct_cwe": row.get("correct_cwe"),
                "matched_cwe": row.get("matched_cwe"),
                "direct_match": row.get("direct_match"),
                "was_changed": "",
                "tp_old": row.get("tp"),
                "fp_old": row.get("fp"),
                "tn_old": row.get("tn"),
                "fn_old": row.get("fn"),
                # Copy old values to new values
                "tp_new": row.get("tp"),
                "fp_new": row.get("fp"),
                "tn_new": row.get("tn"),
                "fn_new": row.get("fn"),
                # Leave LLM-specific fields empty
                "input_token": "",
                "input_cost": "",
                "reasoning_token": "",
                "reasoning_cost": "",
                "output_token": "",
                "output_cost": "",
                "total_cost": "",
                "analysis_time": "",
            }
            final_output_rows.append(unprocessed_row)

    csv_file_name = f"{args.model_name}_{args.prompt_type}_{timestamp}.csv"
    csv_file_path = os.path.join(args.output_folder, csv_file_name)

    try:
        with open(csv_file_path, "w", newline="", encoding="utf-8") as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=CSV_HEADER)
            writer.writeheader()
            writer.writerows(final_output_rows)
    except IOError as e:
        logging.error(f"Could not write to CSV file {csv_file_path}: {e}")

    logging.info("--- Evaluation Complete or Aborted ---")
    logging.info(f"Results saved to: {csv_file_path}")
    logging.info(f"Logs saved to: {log_file_path}")


if __name__ == "__main__":
    main()
