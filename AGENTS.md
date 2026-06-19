# Repository Guidelines

This document serves as a contributor guide for the **yu-ai-agent** repository. It
covers project structure, development workflows, coding conventions, testing, and
contribution practices. Read it before making changes so the codebase stays
consistent and easy to work with.

---

## Project Structure & Module Organization

The repository is a multi-module AI application organized as follows:

```
yu-ai-agent/                  # Root: Spring Boot 3 backend (Java 21, Maven)
├── pom.xml                   # Maven build with Spring Boot 3.4.4
├── Dockerfile                # Production Docker image (Amazon Corretto 21)
├── src/
│   ├── main/java/com/yupi/yuaiagent/    # Backend Java source
│   ├── main/resources/                  # Application configs (YAML profiles)
│   └── test/java/com/yupi/yuaiagent/   # Unit / integration tests
├── yu-ai-agent-frontend/     # Vue 3 + Vite frontend (port 3000)
│   ├── src/api/              # Axios API client
│   ├── src/components/       # Vue components (ChatRoom, AppFooter, etc.)
│   ├── src/router/           # Vue Router config
│   └── src/views/            # Page-level views (Home, LoveMaster, SuperAgent)
└── yu-image-search-mcp-server/  # Standalone MCP server (Spring Boot 3.4.5)
```

| Module               | Framework            | Port  | Key dependency                |
|----------------------|----------------------|-------|-------------------------------|
| Backend (root)       | Spring Boot 3 + Maven | 8123  | Spring AI, DashScope, Ollama  |
| Frontend             | Vue 3 + Vite         | 3000  | Axios, Vue Router             |
| MCP server           | Spring Boot 3 + Maven | —     | Spring AI MCP, Pexels API     |

Add new tools, advisors, or document loaders under `src/main/java/com/yupi/yuaiagent/`
in a package that matches their concern (e.g., `tool`, `advisor`, `rag`).

---

## Build, Test, and Development Commands

### Backend (root)

```bash
# Build the project (skip tests for speed)
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run locally (uses application-local.yml)
./mvnw spring-boot:run

# Run with a specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Frontend (yu-ai-agent-frontend)

```bash
# Install dependencies
npm install

# Start dev server on port 3000
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

### Docker

```bash
# Build and run the backend container
docker build -t yu-ai-agent .
docker run -p 8123:8123 yu-ai-agent
```

---

## Coding Style & Naming Conventions

- **Java**: 4-space indentation. Follow Spring Boot conventions: classes in
  `PascalCase`, methods/variables in `camelCase`, constants in `UPPER_SNAKE_CASE`.
  Package names are lowercase (`com.yupi.yuaiagent.*`).
- **Vue / JavaScript**: 2-space indentation. Use Vue 3 Composition API
  (`<script setup>`). Component files use `PascalCase.vue`. Plain JS files use
  `camelCase`.
- **Configuration**: Profile-specific YAML files follow the
  `application-{profile}.yml` naming pattern. Keep secrets (API keys, database
  URLs) out of tracked configs — use `application-local.yml` (already in
  `.gitignore`) or environment variables.
- **Linting / formatting**: No strict linter is configured. Maintain consistency
  with the surrounding code when making changes.

---

## Testing Guidelines

- **Framework**: JUnit 5 with Spring Boot test support (`@SpringBootTest`).
- **Location**: Tests go in `src/test/java/com/yupi/yuaiagent/`, mirroring the
  source package structure.
- **Naming**: Test classes follow `<ClassUnderTest>Test` convention
  (e.g., `YuAiAgentApplicationTests`).
- **Coverage**: No mandatory coverage threshold, but exercise judgment — add
  tests for tools, advisors, RAG components, and controller endpoints, especially
  when they involve non-trivial logic or external integrations.
- **Run**: `./mvnw test` from the root directory.

---

## Commit & Pull Request Guidelines

- **Commit messages**: Use the imperative mood in English (e.g., `feat: ...`,
  `refactor: ...`, `fix: ...`). For multi-sentence messages, start with a short
  summary line, leave a blank line, then bullet-point the details — the
  existing history shows this pattern clearly.
- **Scope**: Prefix the summary with a conventional commit type (`feat`, `fix`,
  `refactor`, `docs`) when the change is non-trivial. Keep commits focused on a
  single logical change.
- **Pull requests**: Open a PR with a descriptive title. Include a summary of
  what changed and why. Link related issues. For UI changes, add screenshots.
  For API or tool changes, note the affected endpoints or tools.

---

## Security & Configuration Tips

- **API keys** (`dashscope.api-key`, `search-api.api-key`) are placeholders in
  `application.yml`. Replace them in `application-local.yml` (gitignored) or
  via environment variables. Never commit real keys.
- The main application class excludes `DataSourceAutoConfiguration` by default
  to simplify local development. Remove the exclusion only when PgVector is
  available.
- MCP client SSE/STDIO connections and PgVector settings are commented out in
  `application.yml` by default — uncomment them when the respective services
  are running.
