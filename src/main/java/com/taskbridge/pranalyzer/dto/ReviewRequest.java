package com.taskbridge.pranalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotBlank String repositoryOwner,
        @NotBlank String repositoryName,
        @NotNull Integer pullRequestNumber,
        @NotBlank String baseBranch,
        @NotBlank String headBranch,
        String diffText,
        String changedFilesSummary,
        String repositoryContext
) {
}
