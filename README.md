AI-Powered RFP Management System

An end-to-end system that automates the creation, distribution, and evaluation of RFPs (Request for Proposals) using AI (Perplexity Sonar API), Spring Boot, PostgreSQL, and a React + TypeScript frontend.

This system enables procurement teams to:

Generate structured RFPs from natural-language descriptions

Manage vendors

Invite vendors via email

Automatically ingest vendor proposal responses

Parse proposals into structured pricing using AI

View proposals per RFP for comparison

All APIs are fully documented inside the included Postman collection (postman/rfp.postman_collection.json).

📁 Repository Structure
ai-rfp-system/
│
├── backend/             → Spring Boot backend
├── frontend/            → React + Vite + TypeScript frontend
├── postman/             → Postman collection for all backend APIs
├── .env.example         → Example environment variables
├── docker-compose.yml   → PostgreSQL + PgAdmin setup
└── README.md

-----------------------------------------
🖥️ Frontend Setup (React + Vite + TS)
1. Prerequisites

Install:

Node.js v18+

npm

2. Install dependencies
cd frontend
npm install

3. Configuration

The frontend expects the backend to run at:

http://localhost:8080


CORS must allow:

http://localhost:5173

4. Run the frontend
npm run dev


Visit:

http://localhost:5173

5. Build for production
npm run build

6. Directory structure
frontend/src/
├── components   → Reusable UI elements (MainLayout, etc.)
├── pages        → Screens (RFP list, create RFP, RFP details)
├── services     → Backend API wrappers
└── types        → TypeScript interfaces (match backend DTOs)

-----------------------------------------
🛠️ Backend Setup (Spring Boot + PostgreSQL)
1. Requirements

Install:

Java 17+

Maven Wrapper (included)

PostgreSQL (local or via Docker)

2. Backend Installation
cd backend
./mvnw clean install

3. Database Configuration

Backend uses PostgreSQL. Configure in:

backend/src/main/resources/application.yml


Example:

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

3.1 Flyway migrations

All DB schema files are located at:

backend/src/main/resources/db/migration/


Example:

V1__init.sql  → Creates all tables

4. Run Backend
./mvnw spring-boot:run


Expected logs:

Flyway migrations completed
Spring Boot started on port 8080


Test:

http://localhost:8080/api/rfps

-----------------------------------------
🐳 Docker Setup (PostgreSQL + PgAdmin)

The project includes a ready-to-run Docker Compose setup.

Create or update docker-compose.yml:
services:
  postgres:
    image: postgres:16
    container_name: rfp_postgres
    restart: always
    environment:
      POSTGRES_DB: airfp_system
      POSTGRES_USER: airfp_user
      POSTGRES_PASSWORD: airfp_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: my-pgadmin
    restart: always
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@admin.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "8888:80"
    depends_on:
      - postgres
    volumes:
      - pgadmin_data:/var/lib/pgadmin

volumes:
  postgres_data:
  pgadmin_data:

Start containers:
docker-compose up -d


Access PgAdmin:

http://localhost:8888


Add server:

Host: postgres

User: airfp_user

Password: airfp_password

-----------------------------------------
🤖 Perplexity AI Integration

The backend uses Perplexity Sonar Pro for:

Generating RFPs from natural text

Parsing vendor proposal emails

Scoring proposals (optional next step)

1. Setup in application.yml
perplexity:
  api:
    key: ${PERPLEXITY_API_KEY:}
    model: sonar-pro
    base-url: https://api.perplexity.ai
    timeout-seconds: 30

2. Set environment variable
macOS/Linux
export PERPLEXITY_API_KEY="pplx-your-key"

Windows PowerShell
[Environment]::SetEnvironmentVariable("PERPLEXITY_API_KEY", "pplx-your-key", "User")

IntelliJ

Run → Edit Configurations → Environment Variables:

PERPLEXITY_API_KEY=pplx-your-key

-----------------------------------------
📬 Email Sending (Vendor Invitations)

The backend sends RFP invitations to vendors using SMTP.

Add to application.yml:

spring:
  mail:
    host: smtp.mailtrap.io
    port: 587
    username: your-user
    password: your-pass
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true


Endpoint:

POST /api/rfps/{id}/send


Emails include:

RFP details

Items list

Reply instructions

Special subject format for auto-ingest:

RFP #<ID> - <TITLE> [VENDOR:<VENDOR_ID>]

-----------------------------------------
📥 Automatic Email Ingestion (IMAP)

The system can automatically read vendor replies from an inbox.

IMAP config:

app:
  mail:
    imap:
      host: imap.yourserver.com
      port: 993
      username: rfp.system@example.com
      password: yourpassword
      protocol: imaps


A scheduled job runs every minute:

@Scheduled(cron = "0 */1 * * * *")
public void pollInbox()


Steps:

Read unread emails

Extract RFP ID + Vendor ID from subject

Extract body text

Pass to AI: parse pricing, terms, items

Store as Proposal + ProposalItem

-----------------------------------------
📡 API Overview
Action	Method	Endpoint
Create RFP	POST	/api/rfps
Generate RFP from text	POST	/api/rfps/from-text
Get RFP list	GET	/api/rfps
Get RFP details	GET	/api/rfps/{id}
Send RFP to vendors	POST	/api/rfps/{id}/send
Vendor CRUD	/api/vendors	
Ingest vendor email (manual)	POST	/api/proposals/ingest-email
List proposals for an RFP	GET	/api/proposals/by-rfp/{rfpId}
✔ Postman Collection Available

All endpoints are included in:

postman/rfp.postman_collection.json


Import into Postman to test every API.

-----------------------------------------
🔧 Running the Entire System
1. Start PostgreSQL + PgAdmin via Docker
docker-compose up -d

2. Start Backend
cd backend
./mvnw spring-boot:run


Backend runs at:

http://localhost:8080

3. Start Frontend
cd frontend
npm run dev


Frontend runs at:

http://localhost:5173

-----------------------------------------
🚀 Deployment Notes
Frontend

Build with: npm run build

Deploy static files (dist/) to:

Netlify

Vercel

Cloudflare Pages

S3 + CloudFront

Backend

Deploy JAR to:

Render

Railway

AWS EC2

DigitalOcean

ENV variables required:

PERPLEXITY_API_KEY
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

-----------------------------------------
✅ Conclusion

This system provides:

AI-generated RFP creation

Vendor management

Email invitations

Auto-ingestion of responses

AI-powered proposal extraction

Full set of APIs with Postman collection

Dockerized database environment

Clean React frontend UI
