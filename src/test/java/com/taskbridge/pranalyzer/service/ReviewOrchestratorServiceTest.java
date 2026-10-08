package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.dto.ReviewRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReviewOrchestratorServiceTest {

    @Test
    void shouldGenerateReviewResponseWithQualityScore() {
        RuleBasedAnalyzer ruleBasedAnalyzer = new RuleBasedAnalyzer();
        ArchitectureValidator architectureValidator = new ArchitectureValidator();
        DependencySecurityScanner dependencySecurityScanner = new DependencySecurityScanner();
        TestCoverageAnalyzer testCoverageAnalyzer = new TestCoverageAnalyzer();
        AiReviewService aiReviewService = Mockito.mock(AiReviewService.class);
        Mockito.when(aiReviewService.generateSummary(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList()))
                .thenReturn("AI summary ok");

        ReviewOrchestratorService service = new ReviewOrchestratorService(
                ruleBasedAnalyzer,
                architectureValidator,
                dependencySecurityScanner,
                testCoverageAnalyzer,
                aiReviewService
        );

        var response = service.analyze(new ReviewRequest(
                "octo-org",
                "demo-app",
                12,
                "main",
                "feature/test",
                "const apiKey = \"abc123\";\nString query = \"SELECT * FROM users WHERE id = \" + userId;",
                "src/main/java/App.java",
                "test context"
        ));

        assertNotNull(response);
        assertEquals("octo-org", response.repositoryOwner());
        assertNotNull(response.summary());
        assertNotNull(response.aiSummary());
    }
}
