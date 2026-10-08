package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.domain.ReviewCategory;
import com.taskbridge.pranalyzer.domain.ReviewFinding;
import com.taskbridge.pranalyzer.domain.Severity;
import com.taskbridge.pranalyzer.dto.ReviewRequest;
import com.taskbridge.pranalyzer.dto.ReviewResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewOrchestratorService {

    private final RuleBasedAnalyzer ruleBasedAnalyzer;
    private final ArchitectureValidator architectureValidator;
    private final DependencySecurityScanner dependencySecurityScanner;
    private final TestCoverageAnalyzer testCoverageAnalyzer;
    private final AiReviewService aiReviewService;

    public ReviewOrchestratorService(RuleBasedAnalyzer ruleBasedAnalyzer,
                                   ArchitectureValidator architectureValidator,
                                   DependencySecurityScanner dependencySecurityScanner,
                                   TestCoverageAnalyzer testCoverageAnalyzer,
                                   AiReviewService aiReviewService) {
        this.ruleBasedAnalyzer = ruleBasedAnalyzer;
        this.architectureValidator = architectureValidator;
        this.dependencySecurityScanner = dependencySecurityScanner;
        this.testCoverageAnalyzer = testCoverageAnalyzer;
        this.aiReviewService = aiReviewService;
    }

    public ReviewResponse analyze(ReviewRequest request) {
        List<ReviewFinding> findings = new ArrayList<>();

        findings.addAll(ruleBasedAnalyzer.analyze(request.diffText(), request.changedFilesSummary()));
        findings.addAll(architectureValidator.validate(request.diffText()));
        findings.addAll(dependencySecurityScanner.scan(request.diffText()));
        findings.addAll(testCoverageAnalyzer.analyze(request.diffText(), request.changedFilesSummary()));

        findings = findings.stream()
                .sorted(Comparator.comparingInt(this::priorityValue).reversed())
                .toList();

        List<String> severityStrings = findings.stream()
                .map(f -> f.title())
                .toList();

        String aiSummary = aiReviewService.generateSummary(
                request.repositoryOwner(),
                request.repositoryName(),
                request.diffText(),
                severityStrings
        );

        int qualityScore = computeQualityScore(findings);
        String reviewId = UUID.randomUUID().toString();

        return new ReviewResponse(
                reviewId,
                qualityScore,
                buildManualSummary(findings),
                aiSummary,
                findings,
                buildRecommendations(findings),
                request.repositoryOwner(),
                request.repositoryName(),
                request.pullRequestNumber()
        );
    }

    private int computeQualityScore(List<ReviewFinding> findings) {
        int penalty = findings.stream()
                .mapToInt(this::severityWeight)
                .sum();
        int score = 100 - penalty;
        return Math.max(0, Math.min(score, 100));
    }

    private int severityWeight(ReviewFinding finding) {
        return switch (finding.severity()) {
            case CRITICAL -> 25;
            case HIGH -> 15;
            case MEDIUM -> 8;
            case LOW -> 4;
            case INFO -> 2;
        };
    }

    private int priorityValue(ReviewFinding finding) {
        return severityWeight(finding);
    }

    private String buildManualSummary(List<ReviewFinding> findings) {
        if (findings.isEmpty()) {
            return "No obvious code quality or security issues detected by the rule-based review.";
        }

        long critical = findings.stream().filter(f -> f.severity() == Severity.CRITICAL).count();
        long high = findings.stream().filter(f -> f.severity() == Severity.HIGH).count();

        return "The review identified " + findings.size() + " suggested issues, including " + critical + " critical items and " + high + " high-severity findings.";
    }

    private List<String> buildRecommendations(List<ReviewFinding> findings) {
        List<String> recommendations = new ArrayList<>();
        for (ReviewFinding finding : findings) {
            if (finding.suggestion() != null && !finding.suggestion().isBlank()) {
                recommendations.add(finding.suggestion());
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Consider adding a regression test for the changed behavior.");
        }

        return recommendations.stream().distinct().limit(5).toList();
    }
}
