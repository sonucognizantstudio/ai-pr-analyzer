package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.domain.ReviewCategory;
import com.taskbridge.pranalyzer.domain.ReviewFinding;
import com.taskbridge.pranalyzer.domain.Severity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RuleBasedAnalyzer {

    private static final Pattern CREDENTIAL_PATTERN = Pattern.compile("(?i)(api[_-]?key|secret|token|password|passwd)\\s*[:=]\\s*['\"]?[A-Za-z0-9_\\-]{6,}['\"]?", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(?i)(SELECT|INSERT|UPDATE|DELETE).*?(\\+\\s*\\w+|\\$\\{\\w+}|\\w+\\s*\\+\\s*\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DUPLICATE_LINE_PATTERN = Pattern.compile("(?m)^(?:\\s*[/\\*#\\-\\s\\w\\[\\]().,;:'\"=<>+`?]+)$");

    public List<ReviewFinding> analyze(String diffText, String changedFilesSummary) {
        List<ReviewFinding> findings = new ArrayList<>();

        if (diffText == null || diffText.isBlank()) {
            return findings;
        }

        if (containsSensitiveCredentials(diffText)) {
            findings.add(new ReviewFinding(
                    "SEC-001",
                    "Hardcoded credentials detected",
                    "The diff contains a likely hardcoded secret or key value.",
                    Severity.HIGH,
                    ReviewCategory.SECURITY,
                    "unknown",
                    null,
                    "Move the secret to environment variables or a secure secret manager."
            ));
        }

        if (containsSqlInjectionRisk(diffText)) {
            findings.add(new ReviewFinding(
                    "SEC-002",
                    "Potential SQL injection vector",
                    "String concatenation in a SQL statement could allow injection if user-controlled values are used.",
                    Severity.HIGH,
                    ReviewCategory.SECURITY,
                    "unknown",
                    null,
                    "Use parameterized queries and bound variables instead of string concatenation."
            ));
        }

        if (hasDuplicateCodePattern(diffText)) {
            findings.add(new ReviewFinding(
                    "DUP-001",
                    "Duplicate code pattern detected",
                    "The patch repeats the same or very similar logic multiple times, which may indicate copy-paste maintenance risk.",
                    Severity.MEDIUM,
                    ReviewCategory.DUPLICATION,
                    "unknown",
                    null,
                    "Extract the duplicated logic into a helper or shared utility method."
            ));
        }

        if (isMissingTestCoverage(changedFilesSummary, diffText)) {
            findings.add(new ReviewFinding(
                    "TEST-001",
                    "Potentially missing test coverage",
                    "Production code appears to have changed, but there is no clear indication that tests were updated or added.",
                    Severity.MEDIUM,
                    ReviewCategory.TESTING,
                    "unknown",
                    null,
                    "Add or update unit/integration tests to cover the changed behavior."
            ));
        }

        if (looksLikeLargeMonolithicChange(diffText)) {
            findings.add(new ReviewFinding(
                    "ARCH-001",
                    "Large monolithic change detected",
                    "The change appears to mix multiple concerns in a single patch, which can reduce maintainability and make validation harder.",
                    Severity.MEDIUM,
                    ReviewCategory.ARCHITECTURE,
                    "unknown",
                    null,
                    "Split responsibilities into smaller commits or modules and validate each concern individually."
            ));
        }

        return findings;
    }

    private boolean containsSensitiveCredentials(String diffText) {
        Matcher matcher = CREDENTIAL_PATTERN.matcher(diffText);
        return matcher.find();
    }

    private boolean containsSqlInjectionRisk(String diffText) {
        Matcher matcher = SQL_INJECTION_PATTERN.matcher(diffText);
        return matcher.find();
    }

    private boolean hasDuplicateCodePattern(String diffText) {
        String normalized = diffText.toLowerCase(Locale.ROOT);
        return normalized.contains("if (") && normalized.contains("if (") && countOccurrences(normalized, "if (") > 3;
    }

    private boolean isMissingTestCoverage(String changedFilesSummary, String diffText) {
        String combined = (changedFilesSummary == null ? "" : changedFilesSummary) + " " + diffText;
        return combined.toLowerCase(Locale.ROOT).contains("src/main")
                && !combined.toLowerCase(Locale.ROOT).contains("src/test")
                && !combined.toLowerCase(Locale.ROOT).contains("test case");
    }

    private boolean looksLikeLargeMonolithicChange(String diffText) {
        String clean = diffText.replace("\n", " ");
        int ifCount = countOccurrences(clean.toLowerCase(Locale.ROOT), "if ");
        int switchCount = countOccurrences(clean.toLowerCase(Locale.ROOT), "switch ");
        int tryCount = countOccurrences(clean.toLowerCase(Locale.ROOT), "try ");
        return (ifCount + switchCount + tryCount) > 10;
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
    }
}
