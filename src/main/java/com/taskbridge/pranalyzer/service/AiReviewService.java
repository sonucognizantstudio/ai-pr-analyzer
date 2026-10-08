package com.taskbridge.pranalyzer.service;

import com.taskbridge.pranalyzer.config.OpenAiProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AiReviewService {

    private final WebClient openAiWebClient;
    private final OpenAiProperties openAiProperties;

    public AiReviewService(WebClient openAiWebClient, OpenAiProperties openAiProperties) {
        this.openAiWebClient = openAiWebClient;
        this.openAiProperties = openAiProperties;
    }

    public String generateSummary(String repositoryOwner, String repositoryName, String diffText, List<String> findings) {
        if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
            return buildFallbackSummary(repositoryOwner, repositoryName, findings);
        }

        String prompt = "You are a senior code reviewer. Review this PR and summarize the quality, risks, and specific follow-up recommendations. " +
                "Repository: " + repositoryOwner + "/" + repositoryName + "\n" +
                "Diff: " + diffText + "\n" +
                "Findings: " + String.join("; ", findings);

        Map<String, Object> requestBody = Map.of(
                "model", openAiProperties.getModel() != null ? openAiProperties.getModel() : "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You provide concise, actionable PR review comments."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        try {
            String response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(body -> {
                        var choices = (List<Map<String, Object>>) body.get("choices");
                        if (choices == null || choices.isEmpty()) {
                            return "AI summary unavailable.";
                        }
                        Map<String, Object> firstChoice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        return message != null ? (String) message.get("content") : "AI summary unavailable.";
                    })
                    .onErrorResume(ex -> Mono.just(buildFallbackSummary(repositoryOwner, repositoryName, findings)))
                    .block();

            return response == null || response.isBlank() ? buildFallbackSummary(repositoryOwner, repositoryName, findings) : response;
        } catch (Exception ex) {
            return buildFallbackSummary(repositoryOwner, repositoryName, findings);
        }
    }

    private String buildFallbackSummary(String repositoryOwner, String repositoryName, List<String> findings) {
        return "PR review summary for " + repositoryOwner + "/" + repositoryName + ". " +
                (findings.isEmpty() ? "No major issues were detected by rule-based review." :
                        "The review detected several follow-up items, including security and coverage considerations that should be validated before merge.");
    }
}
