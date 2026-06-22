# Repository Guidelines

## Project Structure & Module Organization

```
yu-ai-agent/
├── src/main/java/com/yupi/yuaiagent/   # Spring Boot application source
│   ├── agent/          # AI agents (ReAct, Manus, ToolCall)
│   ├── app/            # Application entry points (LoveApp)
│   ├── chatmemory/     # Chat memory implementations
│   ├── config/         # CORS and app configuration
│   ├── controller/     # REST controllers (AiController, HealthController)
│   ├── advisor/        # Custom advisors (Logger, ReReading)
│   ├── rag/            # RAG: loaders, vector stores, keyword enrichers, query expanders
│   ├── tools/          # Agent tools (file ops, web search, PDF generation, etc.)
│   └── demo/           # Demo code for AI invocation patterns
├── src/test/java/com/yupi/yuaiagent/   # Mirror of src/main, tests per feature
├── yu-ai-agent-frontend/               # Vue 3 + Vite frontend (separate module)
└── yu-image-search-mcp-server/         # Standalone MCP server for image search
```

The backend follows a feature-package layout under `com.yupi.yuaiagent`. Tests mirror main source by package.

## Build, Test, and Development Commands

**Backend (Maven)**
- `./mvnw spring-boot:run` — start the dev server (port 8123)
- `./mvnw test` — run all JUnit tests
- `./mvnw clean package -DskipTests` — package for production

**Frontend**
- `cd yu-ai-agent-frontend && npm run dev` — start Vite dev server
- `cd yu-ai-agent-frontend && npm run build` — production build
- `cd yu-ai-agent-frontend && npm run preview` — preview production build locally

**Docker**
- `docker build -t yu-ai-agent .` — build backend image
- `docker build -t yu-ai-agent-frontend ./yu-ai-agent-frontend` — build frontend image

## Coding Style & Naming Conventions

- **Java**: Follow standard Spring Boot conventions. Use Chinese comments where helpful for domain concepts.
- **Vue**: Single-file components (`.vue`) in PascalCase. API calls centralized in `src/api/index.js`.
- **Indentation**: 4 spaces for Java, 2 spaces for JavaScript/Vue (per Vite defaults).
- **Naming**: Controller classes end in `Controller`; tools extend `ToolRegistration`; tests mirror their source class name with `Test` suffix.

## Testing Guidelines

Tests use JUnit 5 (Spring Boot Test starter). Test classes live in `src/test/java/com/yupi/yuaiagent/` and mirror the production package structure. Run all tests with `./mvnw test`.

Key test areas:
- `agent/` — Agent behavior tests (YuManus)
- `tools/` — Individual tool unit tests
- `app/` — Application integration tests
- `rag/` — RAG pipeline tests (document loading, vector store)

## Commit & Pull Request Guidelines

Commits follow a structured Chinese format:

```
第 X 期 - Topic / Feature
1. Implementation details as numbered list
2. Additional changes
```

Prefix refactors with `refactor:` and include a bulleted summary. Keep PRs scoped to one episode/topic. Link related issues when applicable.

## Architecture Overview

- **Backend**: Spring Boot 3.4.4 on Java 21 with Spring AI Alibaba (1.0.0.2). The application builds AI agents that combine tool calling, RAG, and structured memory.
- **Frontend**: Vue 3 SPA communicating with the backend SSE and REST endpoints.
- **MCP Server**: Standalone Java process implementing the Model Context Protocol for image search, consumed by the main agent runtime.
- **Deployment**: Backend and frontend each have their own Dockerfile. The frontend uses nginx as a reverse proxy in production.
