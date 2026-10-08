package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.domain.ReviewCategory;
import com.taskbridge.pranalyzer.domain.ReviewFinding;
import com.taskbridge.pranalyzer.domain.Severity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DependencySecurityScanner {

    public List<ReviewFinding> scan(String diffText) {
        List<ReviewFinding> findings = new ArrayList<>();

        if (diffText == null || diffText.isBlank()) {
            return findings;
        }

        String lower = diffText.toLowerCase();

        if (lower.contains("http://") && !lower.contains("https://")) {
            findings.add(new ReviewFinding(
                    "SEC-003",
                    "Insecure network transport",
                    "The change uses non-HTTPS network calls, which may expose sensitive data in transit.",
                    Severity.MEDIUM,
                    ReviewCategory.SECURITY,
                    "unknown",
                    null,
                    "Use HTTPS, TLS, or trusted internal transport controls for all communications."
            ));
        }

        if (lower.contains("eval(") || lower.contains("exec(")) {
            findings.add(new ReviewFinding(
                    "SEC-004",
                    "Unsafe dynamic execution pattern",
                    "The code calls dynamic execution functions that can lead to code injection or unsafe runtime evaluation.",
                    Severity.CRITICAL,
                    ReviewCategory.SECURITY,
                    "unknown",
                    null,
                    "Replace dynamic execution with explicit, validated logic and avoid user-controlled command strings."
            ));
        }

        return findings;
    }
}
