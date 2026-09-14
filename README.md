 # docmanager-api

A document / correspondence management REST API built with Spring Boot 3 and Java 21.

## Why this project exists

Since 2015 I've built and supported enterprise EDMS (Electronic Document Management System) and Correspondence Tracking System platforms for government and enterprise clients — most recently at Everteam/Intalio, supporting a multi-site institutional client with zero tolerance for production downtime. That work has been almost entirely Java/J2EE, Spring, and REST/SOAP integration on top of Oracle and SQL Server.

This project re-implements the core of that domain — registering documents, moving them through a review/approval workflow, searching and filtering a correspondence log — on a modern stack: Spring Boot 3, Spring Data JPA, PostgreSQL, Docker, and a CI pipeline that builds, tests, and containerizes the service on every push. It exists to demonstrate that stack hands-on, alongside 17 years of the enterprise Java background above.

## What it does

- Register documents/correspondence with a category, description, owner, and due date, and auto-generate a human-readable reference number (`CTS-2026-000482`), the same convention used by the correspondence-tracking systems this project is modeled on.
- Move a document through a restricted workflow — `DRAFT → PENDING_REVIEW → APPROVED/REJECTED → ARCHIVED` — with illegal transitions (e.g. `DRAFT` straight to `ARCHIVED`) rejected with a `409 Conflict`.
- List, filter by status or category, and search by title, with pagination on every list endpoint.
- Return structured validation errors (`400`) and not-found errors (`404`) as consistent JSON, via a global exception handler.
- Expose interactive API docs at `/swagger-ui.html` (OpenAPI via springdoc) and health/info at `/actuator/health`.

## Tech stack

| Layer | Choice |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 3.3 (Web, Data JPA, Validation, Actuator) |
| Database | PostgreSQL (H2 in-memory for tests) |
| API docs | springdoc-openapi / Swagger UI |
| Tests | JUnit 5, Mockito, AssertJ, Spring `@WebMvcTest` / `@DataJpaTest` |
| Packaging | Docker (multi-stage build), Docker Compose |
| CI | GitHub Actions — `mvn verify` + Docker image build on every push/PR |

## API overview

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/documents` | Create a document (starts in `DRAFT`) |
| `GET` | `/api/documents/{id}` | Fetch a document by id |
| `GET` | `/api/documents` | List documents (`?status=`, `?category=`, `?titleContains=`, paginated) |
| `PUT` | `/api/documents/{id}` | Update a document's editable fields |
| `PATCH` | `/api/documents/{id}/status` | Transition workflow status |
| `DELETE` | `/api/documents/{id}` | Delete a document |

Full request/response schemas are available at `/swagger-ui.html` once the service is running.

## Running it locally

```bash
docker compose up --build
```

This starts PostgreSQL and the API together; the API is then available at `http://localhost:8080`, with Swagger UI at `http://localhost:8080/swagger-ui.html`.

To run against a local Maven/JDK install instead:

```bash
mvn spring-boot:run
```

(defaults to `localhost:5432` for Postgres — override with `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` env vars, or point it at the `docker compose up db` container).

## Running the tests

```bash
mvn clean verify
```

Tests cover the service layer's workflow rules (Mockito), the controller layer's request/response contract (`@WebMvcTest` + MockMvc), and the repository's query methods against a real (in-memory H2) database (`@DataJpaTest`).

## A note on how this was built

This project was authored and code-reviewed in a sandboxed environment without access to Maven Central or start.spring.io, so it was not compiled locally before being pushed here. Correctness is instead verified by the GitHub Actions workflow in `.github/workflows/ci.yml`, which runs `mvn clean verify` (full build + test suite) and a Docker image build on every push — check the **Actions** tab for the current build status rather than taking the code on faith.

## Project structure

```
src/main/java/dev/umairalishah/docmanager/
├── controller/    REST endpoints
├── service/       Workflow rules, orchestration
├── repository/    Spring Data JPA repositories
├── model/         JPA entities and enums
├── dto/           Request/response records
├── exception/      Domain exceptions + global handler
└── config/        OpenAPI configuration
```

## Author

**Umair Ali Shah** — Senior Java Backend Engineer, 17+ years in enterprise/government systems.
LinkedIn: https://www.linkedin.com/in/umairalishah · GitHub: https://github.com/hunairali
