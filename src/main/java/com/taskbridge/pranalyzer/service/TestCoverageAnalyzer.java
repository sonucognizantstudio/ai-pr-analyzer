package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.domain.ReviewCategory;
import com.taskbridge.pranalyzer.domain.ReviewFinding;
import com.taskbridge.pranalyzer.domain.Severity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCoverageAnalyzer {

    public List<ReviewFinding> analyze(String diffText, String changedFilesSummary) {
        List<ReviewFinding> findings = new ArrayList<>();

        String combined = (changedFilesSummary == null ? "" : changedFilesSummary) + " " + (diffText == null ? "" : diffText);
        String lower = combined.toLowerCase();

        boolean hasProductionFiles = lower.contains("src/main");
        boolean hasTestFiles = lower.contains("src/test") || lower.contains("test/");
        boolean mentionsTests = lower.contains("unit test") || lower.contains("integration test") || lower.contains("mock") || lower.contains("assert");

        if (hasProductionFiles && !hasTestFiles && !mentionsTests) {
            findings.add(new ReviewFinding(
                    "TEST-002",
                    "No evidence of tests for production changes",
                    "The patch modifies production behavior but there are no signals that tests or validation were added.",
                    Severity.MEDIUM,
                    ReviewCategory.TESTING,
                    "unknown",
                    null,
                    "Add focused unit tests for the changed logic and include regression checks for edge cases."
            ));
        }

        return findings;
    }
}
