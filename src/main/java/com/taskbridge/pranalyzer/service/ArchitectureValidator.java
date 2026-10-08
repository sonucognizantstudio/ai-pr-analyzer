package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.domain.ReviewCategory;
import com.taskbridge.pranalyzer.domain.ReviewFinding;
import com.taskbridge.pranalyzer.domain.Severity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArchitectureValidator {

    public List<ReviewFinding> validate(String diffText) {
        List<ReviewFinding> findings = new ArrayList<>();

        if (diffText == null || diffText.isBlank()) {
            return findings;
        }

        if (diffText.contains("public class") && diffText.contains("new ") && diffText.contains("static")) {
            findings.add(new ReviewFinding(
                    "ARCH-002",
                    "Possible violation of separation of concerns",
                    "This patch blends infrastructure concerns with domain logic in a way that may make service boundaries unclear.",
                    Severity.MEDIUM,
                    ReviewCategory.ARCHITECTURE,
                    "unknown",
                    null,
                    "Keep business logic in service/domain classes and move infrastructure concerns to adapters or configuration classes."
            ));
        }

        if (diffText.toLowerCase().contains("logger.info") && diffText.toLowerCase().contains("sql")) {
            findings.add(new ReviewFinding(
                    "ARCH-003",
                    "Logging and data access are too tightly coupled",
                    "The change logs internal SQL or data access details directly in business logic, which can become noisy and leak implementation details.",
                    Severity.LOW,
                    ReviewCategory.MAINTAINABILITY,
                    "unknown",
                    null,
                    "Use structured logging and keep data access concerns inside repository or persistence components."
            ));
        }

        return findings;
    }
}
