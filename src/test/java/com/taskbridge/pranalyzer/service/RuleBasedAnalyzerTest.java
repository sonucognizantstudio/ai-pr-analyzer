package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.domain.ReviewFinding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuleBasedAnalyzerTest {

    private final RuleBasedAnalyzer analyzer = new RuleBasedAnalyzer();

    @Test
    void shouldDetectHardcodedCredentials() {
        String diff = "const apiKey = \"super-secret-123\";\nconst token = 'abc123';";

        var findings = analyzer.analyze(diff, "src/main/java/App.java");

        assertFalse(findings.isEmpty());
        assertNotNull(findings.stream().filter(f -> f.ruleId().equals("SEC-001")).findFirst().orElse(null));
    }

    @Test
    void shouldDetectSqlInjectionRisk() {
        String diff = "String query = \"SELECT * FROM users WHERE id = \" + userId;";

        var findings = analyzer.analyze(diff, "src/main/java/UserRepository.java");

        assertFalse(findings.isEmpty());
        assertNotNull(findings.stream().filter(f -> f.ruleId().equals("SEC-002")).findFirst().orElse(null));
    }
}
