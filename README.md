# AI PR Analyzer

A Java 21 / Spring Boot application that automatically analyzes pull requests, checks common code quality risks, and summarizes review findings with optional AI-assisted commentary.

## Architecture at a glance
- Controller layer: incoming HTTP requests
- Service layer: orchestrates GitHub retrieval, analysis, and summary generation
- Rule-based analysis: catches security/quality issues without external dependencies
- AI summary layer: uses OpenAI-compatible APIs when configured
- Redis + PostgreSQL: stores and caches review context

## Stack
- Java 21
- Spring Boot 3.3.4
- Spring Data JPA
- PostgreSQL
- Redis
- Docker Compose
- GitHub API client via WebClient
- OpenAI-compatible model integration

## Local run
```bash
docker compose up -d postgres redis
mvn spring-boot:run
```

## Example request
```bash
curl -X POST http://localhost:8080/api/reviews/analyze \
  -H 'Content-Type: application/json' \
  -d '{
    "repositoryOwner": "octo-org",
    "repositoryName": "demo-app",
    "pullRequestNumber": 42,
    "baseBranch": "main",
    "headBranch": "feature/review-support",
    "diffText": "const apiKey = \"abc123\";\nString sql = \"SELECT * FROM users WHERE id = \" + userId;",
    "changedFilesSummary": "src/main/java/UserService.java\nsrc/main/java/UserRepository.java"
  }'
```

## Endpoints
- `POST /api/reviews/analyze`
- `GET /api/reviews/latest`
- `GET /health`
- `GET /swagger-ui.html`

## Notes
The service is intentionally resilient: if the AI API is not configured, it still returns a useful review based on rule-based checks.
