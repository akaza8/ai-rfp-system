# AI-Powered RFP Management System

An intelligent full-stack platform that automates the creation, distribution, and evaluation of **Requests for Proposals (RFPs)** using **AI (Perplexity Sonar API)**, **Spring Boot**, **PostgreSQL**, and a **React + TypeScript** frontend.

This system enables procurement teams to:

- Generate structured RFPs from natural-language input  
- Manage vendors  
- Send RFP invitations via email  
- Automatically ingest vendor proposal replies  
- Use AI to parse unstructured vendor emails into structured proposals  
- Compare proposals linked to each RFP  

A complete Postman collection is available inside:

postman/rfp.postman_collection.json

yaml
Copy code

---

# 📁 Project Structure

ai-rfp-system/
│
├── backend/ → Spring Boot backend
├── frontend/ → React + Vite + TypeScript frontend
├── postman/ → Postman collection
├── docker-compose.yml → PostgreSQL + PgAdmin setup
├── .env.example → Example environment variables
└── README.md → Project documentation

yaml
Copy code

---

# 🖥️ Frontend Setup (React + Vite + TypeScript)

## 1. Prerequisites

Install:

- Node.js v18+
- npm

## 2. Install dependencies

```bash
cd frontend
npm install
3. Configuration
The frontend communicates with the backend at:

arduino
Copy code
http://localhost:8080
Backend must allow CORS from:

arduino
Copy code
http://localhost:5173
4. Run locally
bash
Copy code
npm run dev
Then open:

arduino
Copy code
http://localhost:5173
5. Build for production
bash
Copy code
npm run build
6. Folder structure
css
Copy code
frontend/src/
├── components/     → UI components
├── pages/          → Screens (RFP list, create RFP, details)
├── services/       → Backend API layer
└── types/          → TypeScript DTOs
🛠️ Backend Setup (Spring Boot + PostgreSQL + Flyway)
1. Requirements
Install:

Java 17+

Maven (or wrapper included)

PostgreSQL or Docker

2. Install backend
bash
Copy code
cd backend
./mvnw clean install
3. Configure database
Modify:

css
Copy code
backend/src/main/resources/application.yml
Example:

yaml
Copy code
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
4. Flyway Migrations
Migration files live in:

swift
Copy code
backend/src/main/resources/db/migration/
Including:

pgsql
Copy code
V1__init.sql
These run automatically on application startup.

5. Run backend
bash
Copy code
./mvnw spring-boot:run
Backend runs at:

arduino
Copy code
http://localhost:8080
🐳 Docker Setup (PostgreSQL + PgAdmin)
The project includes a Docker Compose environment for local PostgreSQL.

docker-compose.yml
yaml
Copy code
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
Start environment
bash
Copy code
docker-compose up -d
Access PgAdmin
Open:

arduino
Copy code
http://localhost:8888
Add a server:

Host: postgres

Username: airfp_user

Password: airfp_password

🤖 Perplexity AI Integration
The backend integrates Perplexity Sonar Pro for:

RFP generation from text

Parsing vendor proposal emails

Future scoring workflows

1. Add to application.yml
yaml
Copy code
perplexity:
  api:
    key: ${PERPLEXITY_API_KEY:}
    model: sonar-pro
    base-url: https://api.perplexity.ai
    timeout-seconds: 30
2. Set environment variable
Mac/Linux
bash
Copy code
export PERPLEXITY_API_KEY="pplx-your-key"
Windows PowerShell
powershell
Copy code
[Environment]::SetEnvironmentVariable("PERPLEXITY_API_KEY", "pplx-your-key", "User")
IntelliJ IDEA
Run → Edit Configurations → Environment Variables:

ini
Copy code
PERPLEXITY_API_KEY=pplx-your-key
📬 Email Sending (Vendor Invitations)
Configure SMTP:

yaml
Copy code
spring:
  mail:
    host: smtp.mailtrap.io
    port: 587
    username: your-user
    password: your-pass
API:

bash
Copy code
POST /api/rfps/{id}/send
Invitations include subject format:

ruby
Copy code
RFP #<ID> - <TITLE> [VENDOR:<VENDOR_ID>]
This allows automatic matching of vendor replies.

📥 Automatic Email Ingestion (IMAP)
The backend supports auto reading vendor replies.

IMAP Config
yaml
Copy code
app:
  mail:
    imap:
      host: imap.yourserver.com
      port: 993
      username: rfp.system@example.com
      password: yourpassword
      protocol: imaps
A scheduled job polls every minute:

java
Copy code
@Scheduled(cron = "0 */1 * * * *")
public void pollInbox() { ... }
Emails are parsed by AI into structured proposals.

📡 API Overview
Feature	Method	Endpoint
Create RFP	POST	/api/rfps
Generate RFP from natural text	POST	/api/rfps/from-text
List RFPs	GET	/api/rfps
Get RFP details	GET	/api/rfps/{id}
Send RFP to vendors	POST	/api/rfps/{id}/send
Vendor CRUD	Various	/api/vendors
Ingest vendor proposal (manual)	POST	/api/proposals/ingest-email
List proposals by RFP	GET	/api/proposals/by-rfp/{rfpId}

📦 Postman Collection
Import:

bash
Copy code
postman/rfp.postman_collection.json
Contains all API endpoints organized by feature.

🚀 Running the Entire Application
1. Start Docker Services
bash
Copy code
docker-compose up -d
2. Start Backend
bash
Copy code
cd backend
./mvnw spring-boot:run
3. Start Frontend
bash
Copy code
cd frontend
npm run dev
Access the system:
Frontend → http://localhost:5173

Backend → http://localhost:8080

PgAdmin → http://localhost:8888

📦 Deployment Overview
Frontend Deployment
Build: npm run build

Deploy dist/ folder to:

Vercel

Netlify

Cloudflare Pages

AWS S3

Backend Deployment
Deploy JAR to:

Render

Railway

AWS EC2

DigitalOcean

Required environment variables:

nginx
Copy code
PERPLEXITY_API_KEY
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
✅ Conclusion
This project delivers a complete, AI-powered procurement workflow:

AI-generated RFP creation

Vendor management

Email invitation workflow

Automatic email ingestion

AI proposal parsing

Dockerized infrastructure

Full Postman API coverage
