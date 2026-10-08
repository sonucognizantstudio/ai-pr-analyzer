package com.taskbridge.pranalyzer.dto;

import com.taskbridge.pranalyzer.domain.ReviewFinding;
import java.util.List;

public record ReviewResponse(
        String reviewId,
        Integer qualityScore,
        String summary,
        String aiSummary,
        List<ReviewFinding> findings,
        List<String> recommendations,
        String repositoryOwner,
        String repositoryName,
        Integer pullRequestNumber
) {
}
