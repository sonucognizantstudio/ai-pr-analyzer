package com.taskbridge.pranalyzer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DependencySecurityScannerTest {

    private final DependencySecurityScanner scanner = new DependencySecurityScanner();

    @Test
    void shouldDetectInsecureHttpUsage() {
        var findings = scanner.scan("HttpURLConnection url = new HttpURLConnection(\"http://example.com\");");
        assertFalse(findings.isEmpty());
        assertNotNull(findings.stream().filter(f -> f.ruleId().equals("SEC-003")).findFirst().orElse(null));
    }
}
