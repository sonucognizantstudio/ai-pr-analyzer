package com.taskbridge.pranalyzer.controller;

import com.taskbridge.pranalyzer.dto.ReviewRequest;
import com.taskbridge.pranalyzer.dto.ReviewResponse;
import com.taskbridge.pranalyzer.service.ReviewOrchestratorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class PullRequestReviewController {

    private final ReviewOrchestratorService reviewOrchestratorService;

    public PullRequestReviewController(ReviewOrchestratorService reviewOrchestratorService) {
        this.reviewOrchestratorService = reviewOrchestratorService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ReviewResponse> analyze(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewOrchestratorService.analyze(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<String> latest() {
        return ResponseEntity.ok("The analyzer is running and ready to review pull requests.");
    }
}
