package com.taskbridge.pranalyzer.domain;

public record ReviewFinding(
        String ruleId,
        String title,
        String description,
        Severity severity,
        ReviewCategory category,
        String file,
        Integer line,
        String suggestion
) {
}
