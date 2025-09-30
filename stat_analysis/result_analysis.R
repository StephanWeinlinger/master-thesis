library(tidyverse)  
library(lme4)  
library(emmeans)  
library(multcomp)  
library(janitor)
library(dplyr)

emm_summary_block <- function(fit, spec, fac_name, label, weights = "proportional",  
                              outdir = NULL, adjust = "holm") {  
  em <- emmeans(fit, spec = spec, type = "response", weights = weights)  
  em_df <- as.data.frame(summary(em))  
  
  value_col <- intersect(c("prob", "response", "emmean"), names(em_df))  
  if (length(value_col) == 0) stop("Could not find the estimate column in emmeans summary")  
  value_col <- value_col[1]  
  
  lcl_col <- if ("asymp.LCL" %in% names(em_df)) "asymp.LCL" else if ("lower.CL" %in% names(em_df)) "lower.CL" else NA  
  ucl_col <- if ("asymp.UCL" %in% names(em_df)) "asymp.UCL" else if ("upper.CL" %in% names(em_df)) "upper.CL" else NA  
  
  em_df <- em_df[order(-em_df[[value_col]]), ]  
  em_df$rank <- seq_len(nrow(em_df))  
  
  cat("\n", label, " - estimated marginal means (type = 'response'; weights = '", weights, "'):\n", sep = "")  
  keep <- c(fac_name, value_col, "SE", lcl_col, ucl_col, "rank")  
  keep <- keep[keep %in% names(em_df)]  
  print(em_df[, keep, drop = FALSE])  
  
  pw <- pairs(em, adjust = adjust)  
  cat("\n", label, " - pairwise comparisons (adjust = '", adjust, "'):\n", sep = "")  
  print(as.data.frame(summary(pw)))  
  
  cl <- cld(em, adjust = adjust, Letters = letters)  
  cl_df <- as.data.frame(cl)  
  cl_df <- cl_df[order(-cl_df[[value_col]]), ]  
  
  cat("\n", label, " - ranking with compact letter display (adjust = '", adjust, "'):\n", sep = "")  
  keep2 <- c(fac_name, value_col, ".group")  
  keep2 <- keep2[keep2 %in% names(cl_df)]  
  print(cl_df[, keep2, drop = FALSE])  
  
  if (!is.null(outdir)) {  
    dir.create(outdir, showWarnings = FALSE, recursive = TRUE)  
    readr::write_csv(em_df, file.path(outdir, paste0("emm_", fac_name, ".csv")))  
    readr::write_csv(as.data.frame(summary(pw)), file.path(outdir, paste0("pairs_", fac_name, "_", adjust, ".csv")))  
    readr::write_csv(cl_df, file.path(outdir, paste0("cld_", fac_name, "_", adjust, ".csv")))  
  }  
  
  invisible(list(emm = em_df, pairs = as.data.frame(summary(pw)), cld = cl_df))  
}  

df_raw <- read_csv("all_results_merged_sast.csv", show_col_types = FALSE) %>%  
  clean_names()  

prompt_levels <- c("zero-shot", "few-shot", "cot")  

df_all <- df_raw %>%  
  mutate(  
    model = factor(model),  
    prompt = factor(prompt, levels = intersect(prompt_levels, unique(prompt))),  
    cwe = factor(cwe),  
    test_name = factor(test_name),  
    tp = as.integer(tp), fp = as.integer(fp),  
    tn = as.integer(tn), fn = as.integer(fn),  
    outcome_sum = tp + fp + tn + fn  
  ) %>%  
  filter(outcome_sum == 1L) %>%  
  mutate(  
    correct = as.integer((tp + tn) == 1L),  
    truth   = factor(if_else((tp + fn) == 1L, "positive", "negative"))  
  )  

cat("Rows:", nrow(df_all),  
    "\nUnique tests:", n_distinct(df_all$test_name),  
    "\nModels:", n_distinct(df_all$model),  
    "\nPrompts present:", paste(levels(droplevels(df_all$prompt)), collapse = ", "),  
    "\nCWEs:", n_distinct(df_all$cwe), "\n")  

fit_all <- glmer(  
  correct ~ model + prompt + cwe + truth + (1 | test_name),  
  data = df_all,  
  family = binomial,  
  control = glmerControl(optimizer = "bobyqa", optCtrl = list(maxfun = 2e5))  
)  

cat("\nLikelihood-ratio tests for main effects (ALL data):\n")  
print(drop1(fit_all, test = "Chisq"))  

dir.create("results/all", recursive = TRUE, showWarnings = FALSE)  

res_model_all <- emm_summary_block(  
  fit_all, ~ model, "model",  
  "Model (ALL data; averaged over observed prompt/CWE mix)",  
  weights = "proportional", outdir = "results/all", adjust = "holm"  
)  

res_cwe_all <- emm_summary_block(  
  fit_all, ~ cwe, "cwe",  
  "CWE (ALL data; averaged over observed model/prompt mix)",  
  weights = "proportional", outdir = "results/all", adjust = "holm"  
)  

dir.create("results/all_prompts", recursive = TRUE, showWarnings = FALSE)  

df_zf <- dplyr::filter(df_all, prompt %in% c("zero-shot","few-shot"))
fit_zf <- glmer(correct ~ model + prompt + cwe + truth + (1 | test_name),
                data = df_zf, family = binomial,
                control = glmerControl(optimizer="bobyqa", optCtrl=list(maxfun=2e5)))

cat("\nLikelihood-ratio tests for main effects (ALL data):\n")  
print(drop1(fit_zf, test = "Chisq")["prompt", , drop = FALSE])

em_prompt_zero_few <- emmeans(fit_zf, ~ prompt, type = "response", weights = "proportional")  

em_prompt_zero_few_df <- as.data.frame(summary(em_prompt_zero_few)) %>%  
  arrange(desc(prob)) %>%  
  mutate(rank = row_number())  

cat("\nPrompt (ALL models) - zero-shot vs few-shot EMMs (type='response'; weights='equal'):\n")  
print(em_prompt_zero_few_df[, c("prompt", "prob", "SE",  
                                if ("asymp.LCL" %in% names(em_prompt_zero_few_df)) "asymp.LCL" else "lower.CL",  
                                if ("asymp.UCL" %in% names(em_prompt_zero_few_df)) "asymp.UCL" else "upper.CL",  
                                "rank"), drop = FALSE])  

pw_prompt_zero_few <- pairs(em_prompt_zero_few, adjust = "holm")  
pw_zero_few_df <- as.data.frame(summary(pw_prompt_zero_few))  

cat("\nPrompt (ALL models) - pairwise comparison (zero-shot vs few-shot; adjust='holm'):\n")  
print(pw_zero_few_df)  

cl_prompt_zero_few <- cld(em_prompt_zero_few, adjust = "holm", Letters = letters)  
cl_zero_few_df <- as.data.frame(cl_prompt_zero_few) %>%  
  arrange(desc(prob))  

cat("\nPrompt (ALL models) - ranking (zero-shot vs few-shot) with compact letter display:\n")  
print(cl_zero_few_df[, c("prompt", "prob", ".group"), drop = FALSE])  

readr::write_csv(em_prompt_zero_few_df, "results/all_prompts/emm_prompt_zero_vs_few.csv")  
readr::write_csv(pw_zero_few_df, "results/all_prompts/pairs_prompt_zero_vs_few.csv")  
readr::write_csv(cl_zero_few_df, "results/all_prompts/cld_prompt_zero_vs_few.csv")  

prompt_counts_by_model <- df_all %>%  
  distinct(model, prompt) %>%  
  count(model, name = "n_prompts")  

all_prompts_n <- n_distinct(df_all$prompt)  

balanced_models <- prompt_counts_by_model %>%  
  filter(n_prompts == all_prompts_n) %>%  
  pull(model) %>%  
  as.character()  

cat("\nModels with all prompts (balanced for prompt comparison):\n")  
print(balanced_models)  

df_bal <- df_all %>% filter(model %in% balanced_models)  

fit_prompt_bal <- glmer(  
  correct ~ model + prompt + cwe + truth + (1 | test_name),  
  data = df_bal, family = binomial,  
  control = glmerControl(optimizer = "bobyqa", optCtrl = list(maxfun = 2e5))  
)  

cat("\nLikelihood-ratio test for prompt (BALANCED subset only):\n")  
print(drop1(fit_prompt_bal, test = "Chisq")["prompt", , drop = FALSE])  

dir.create("results/balanced", recursive = TRUE, showWarnings = FALSE)  
res_prompt_bal <- emm_summary_block(  
  fit_prompt_bal, ~ prompt, "prompt",  
  "Prompt (BALANCED models only; averaged over observed CWE mix)",  
  weights = "proportional", outdir = "results/balanced", adjust = "holm"  
)  

agg <- df_all %>%  
  group_by(model, prompt, cwe) %>%  
  summarize(  
    n = n(),  
    accuracy = mean(correct),  
    recall = sum(tp) / pmax(1, sum(tp + fn)),
    specificity = sum(tn) / pmax(1, sum(tn + fp)),
    precision = sum(tp) / pmax(1, sum(tp + fp)),  
    f1 = if_else((precision + recall) > 0, 2*precision*recall/(precision + recall), NA_real_),  
    .groups = "drop"  
  )  
dir.create("results", recursive = TRUE, showWarnings = FALSE)  
write_csv(agg, "results/aggregated_metrics_by_model_prompt_cwe.csv")  
