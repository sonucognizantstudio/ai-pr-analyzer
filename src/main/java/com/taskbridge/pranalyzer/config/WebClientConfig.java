package com.taskbridge.pranalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient githubWebClient(@Value("${github.api.base-url:https://api.github.com}") String githubBaseUrl,
                                    @Value("${github.api.token:}") String githubToken) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(githubBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (githubToken != null && !githubToken.isBlank()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken);
        }

        return builder.build();
    }

    @Bean
    public WebClient openAiWebClient(@Value("${app.ai.openai.base-url:https://api.openai.com/v1}") String baseUrl,
                                    @Value("${app.ai.openai.api-key:}") String apiKey) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        }

        return builder.build();
    }
}
