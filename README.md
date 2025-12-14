# AI-Powered RFP Management System

## for quick start jump to the summary section for a quick guide.
An intelligent full-stack platform that automates the creation, distribution, and evaluation of Requests for Proposals (RFPs) using AI (Perplexity Sonar API), Spring Boot, PostgreSQL, and a React + Vite frontend.

Key capabilities:
- Generate structured RFPs from natural-language input
- Manage vendors and invitations
- Send RFP invitations via email (SMTP)
- Automatically ingest vendor replies (IMAP)
- Parse unstructured vendor emails into structured proposals using AI
- Compare proposals linked to each RFP

Postman collection: `postman/rfp.postman_collection.json`

---

## Table of contents
- [Project structure](#project-structure)
- [Quick start](#quick-start)
  - [Prerequisites](#prerequisites)
  - [Start with Docker (recommended local setup)](#start-with-docker-recommended-local-setup)
  - [Run backend](#run-backend)
  - [Run frontend](#run-frontend)
- [Configuration](#configuration)
  - [Environment variables](#environment-variables)
  - [Backend application.yml examples](#backend-applicationyml-examples)
  - [Perplexity (AI) integration](#perplexity-ai-integration)
  - [Email (SMTP) and IMAP ingestion](#email-smtp-and-imap-ingestion)
- [API overview](#api-overview)
- [Postman collection](#postman-collection)
- [Deployment notes](#deployment-notes)
- [Development notes](#development-notes)
- [License](#license)

---

## Project structure
Top-level layout:

ai-rfp-system/
- backend/         → Spring Boot backend (Java 17+, Maven)
- frontend/        → React + Vite + TypeScript frontend
- postman/         → Postman collection (API examples)
- docker-compose.yml
- .env.example
- README.md

---

## Quick start

### Prerequisites
- Java 17+
- Maven (or use included wrapper `./mvnw`)
- Node 18+
- npm
- Docker & docker-compose (recommended for local DB)

### Start with Docker (recommended local setup)
This starts PostgreSQL + PgAdmin used by the backend.

1. Start services:
```bash
cd backend
docker-compose up -d
```

2. Verify:
- PostgreSQL: localhost:5432
- PgAdmin: http://localhost:8888 (default: admin@admin.com / admin)

Add server in PgAdmin:
- Host: postgres
- Username: airfp_user
- Password: airfp_password

### Run backend
1. Build:
```bash
cd backend
./mvnw clean install
```

2. Run:
```bash
./mvnw spring-boot:run
```

Backend default URL: http://localhost:8080

### Run frontend
1. Install and start:
```bash
cd frontend
npm install
npm run dev
```

Frontend default URL: http://localhost:5173

---

## Configuration

### Environment variables
Use `.env` (not committed) or platform environment variables for deployment. Example keys used by the app:
- PERPLEXITY_API_KEY — Perplexity Sonar API key
- SPRING_DATASOURCE_URL — JDBC URL for PostgreSQL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_MAIL_HOST / SPRING_MAIL_PORT / SPRING_MAIL_USERNAME / SPRING_MAIL_PASSWORD (SMTP)
- APP_MAIL_IMAP_HOST / APP_MAIL_IMAP_PORT / APP_MAIL_IMAP_USERNAME / APP_MAIL_IMAP_PASSWORD (IMAP)

A minimal `.env.example` is included in the repo.

### Backend `application.yml` examples
Example datasource (backend/src/main/resources/application.yml):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/airfp_system
    username: airfp_user
    password: airfp_password

  jpa:
    hibernate:
      ddl-auto: validate

flyway:
  enabled: true
  baseline-on-migrate: true
```

### Perplexity (AI) integration
Configure Perplexity Sonar API in `application.yml`:
```yaml
perplexity:
  api:
    key: ${PERPLEXITY_API_KEY:}
    model: sonar-pro
    base-url: https://api.perplexity.ai
    timeout-seconds: 30
```

Set environment variable:
- macOS/Linux:
```bash
export PERPLEXITY_API_KEY="pplx-your-key"
```
- Windows PowerShell:
```powershell
[Environment]::SetEnvironmentVariable("PERPLEXITY_API_KEY", "pplx-your-key", "User")
```

The backend uses Perplexity to:
- Generate RFPs from free text
- Parse vendor emails into structured proposals
- (Future) scoring workflows

### Email (SMTP) and IMAP ingestion
SMTP (for sending invitations):
```yaml
spring:
  mail:
    host: smtp.mailtrap.io
    port: 587
    username: your-user
    password: your-pass
```

IMAP (for ingesting vendor replies):
```yaml
app:
  mail:
    imap:
      host: imap.yourserver.com
      port: 993
      username: rfp.system@example.com
      password: yourpassword
      protocol: imaps
```

A scheduled job polls the configured IMAP inbox every minute:
```java
@Scheduled(cron = "0 */1 * * * *")
public void pollInbox() { ... }
```

Invitation email subject format:
```
RFP #<ID> - <TITLE> [VENDOR:<VENDOR_ID>]
```
This format enables automatic matching of incoming vendor replies to the correct RFP and vendor.

---

## API overview
Main endpoints (backend base: /api):
- Create RFP: POST /api/rfps
- Generate RFP from natural text: POST /api/rfps/from-text
- List RFPs: GET /api/rfps
- Get RFP details: GET /api/rfps/{id}
- Send RFP to vendors: POST /api/rfps/{id}/send
- Vendor CRUD: /api/vendors (various)
- Ingest vendor proposal (manual): POST /api/proposals/ingest-email
- List proposals by RFP: GET /api/proposals/by-rfp/{rfpId}

Refer to the included Postman collection for full request/response examples.

---

## Postman collection
Import `postman/rfp.postman_collection.json` into Postman to explore the API, test endpoints, and run example flows.

---

## Running the entire application (summary)
1. Start Docker services:
```bash
cd backend
docker-compose up -d
```
2. Start backend:
```bash
cd backend
./mvnw spring-boot:run
```
3. Start frontend:
```bash
cd frontend
npm run dev
```
Open:
- Frontend → http://localhost:5173
- Backend → http://localhost:8080
- PgAdmin → http://localhost:8888

Open:
  - application.yaml
    . set mailtrap username and pass
    . set perplexity api_key
---

## Deployment notes

Frontend:
- Build: `npm run build`
- Deploy `dist/` to Vercel, Netlify, Cloudflare Pages, S3, etc.

Backend:
- Build a JAR and deploy to Render, Railway, AWS EC2, DigitalOcean, or similar.
- Required env vars for production:
  - PERPLEXITY_API_KEY
  - SPRING_DATASOURCE_URL
  - SPRING_DATASOURCE_USERNAME
  - SPRING_DATASOURCE_PASSWORD
  - SMTP/IMAP credentials as needed

---

## Development notes
- Flyway migrations are located in `backend/src/main/resources/db/migration/` (e.g., `V1__init.sql`) and run automatically on startup.
- Frontend code lives under `frontend/src/`:
  - components/, pages/, services/, types/
- Backend uses scheduled mail ingestion; tune scheduling and IMAP settings for production.

---

## Contributing
Contributions are welcome. Create issues for bugs or enhancement requests. For larger changes, please open a PR with a clear description and tests where applicable.

---
---
