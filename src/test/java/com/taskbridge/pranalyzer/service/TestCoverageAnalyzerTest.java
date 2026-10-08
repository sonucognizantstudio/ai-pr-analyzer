package com.taskbridge.pranalyzer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TestCoverageAnalyzerTest {

    private final TestCoverageAnalyzer analyzer = new TestCoverageAnalyzer();

    @Test
    void shouldFlagProductionChangeWithoutTestEvidence() {
        var findings = analyzer.analyze("void updateUser() { }", "src/main/java/UserService.java");
        assertFalse(findings.isEmpty());
    }
}
