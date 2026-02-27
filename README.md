# CorpAI Banking Intelligence Platform

> AI-powered corporate banking sales intelligence system for **PKO BP** corporate banking advisors.

---

## Overview

CorpAI is an **agentic AI assistant** that empowers corporate banking advisors by automatically analyzing companies before client meetings and proactively detecting sales opportunities.

### What CorpAI Does

- 🔍 **Analyzes companies** by NIP/KRS number using multiple external data sources
- 📄 **Generates One Pager briefs** and full reports before client meetings  
- 💡 **Detects sales opportunities** (credit maturities, FX exposure, leasing, ESG financing, etc.)
- 🔒 **Performs AML/KYC checks** with Go/No-Go decision
- 📢 **Sends proactive notifications** to advisors (V3)

---

## Architecture

```
                          ┌─────────────────────────────────────────────┐
                          │              SIB ZONE (inside bank)         │
                          │                                             │
 Advisor ──► Gateway ──►  │  Orchestrator ──► Company Profile Agent    │
 (8079)      JWT Auth     │      │        ──► Financial Analysis Agent  │
                          │      │        ──► AML/KYC Agent             │
                          │      │        ──► Sales Insight Engine      │
                          │      │        ──► Report Generator          │
                          │      │        ──► Notification Service (V3) │
                          │      │                                       │
                          │      └──── Kafka Events ──────────────────  │
                          └─────────────────────────────────────────────┘
                                          │
                          ┌───────────────▼─────────────────────────────┐
                          │        OUTSIDE SIB ZONE                     │
                          │                                             │
                          │  Data Collector ──► KRS API                │
                          │                ──► CRBR API               │
                          │                ──► EMIS                    │
                          │                ──► News Crawlers           │
                          │                ──► LinkedIn                │
                          │                ──► Tender portals          │
                          │                                             │
                          │  LLM Gateway ──► GPT-4 / Azure OpenAI     │
                          │  (Data sanitized before sending!)          │
                          └─────────────────────────────────────────────┘
```

---

## Modules

| Module | Port | Description |
|--------|------|-------------|
| `corpai-gateway` | 8079 | Spring Cloud Gateway – API entry point, JWT auth, rate limiting |
| `corpai-orchestrator` | 8080 | Central orchestrator – coordinates all agents via Kafka |
| `corpai-company-profile` | 8081 | Company profile agent – KRS/CRBR data, Redis cache |
| `corpai-financial-analysis` | 8082 | Financial analysis – ratios, credit maturities, ratings |
| `corpai-aml-kyc` | 8083 | AML/KYC checks – red flags, ownership graph, Go/No-Go |
| `corpai-sales-insight` | 8084 | Sales opportunity detection and scoring engine |
| `corpai-report-generator` | 8085 | One Pager + Full Report HTML generation |
| `corpai-data-collector` | 8086 | External data collector (outside SIB zone) |
| `corpai-llm-gateway` | 8087 | LLM abstraction (GPT-4/Azure OpenAI, data sanitization) |
| `corpai-notification` | 8089 | Proactive advisor notifications (V3) |
| `corpai-common` | – | Shared domain model and Kafka events |

---

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **Docker & Docker Compose**

---

## Quick Start

### 1. Start Infrastructure
```bash
docker compose up -d postgres redis zookeeper kafka kafka-ui elasticsearch prometheus grafana
```

### 2. Build the Project
```bash
mvn clean install -DskipTests
```

### 3. Start Services (development)
```bash
# Start each service in a separate terminal or use docker compose
mvn spring-boot:run -pl corpai-orchestrator
mvn spring-boot:run -pl corpai-company-profile
# ... etc
```

### 4. Access Services
- **API Gateway**: http://localhost:8079
- **Kafka UI**: http://localhost:8090
- **Grafana**: http://localhost:3000 (admin/corpai_admin)
- **Prometheus**: http://localhost:9090

---

## API Endpoints

| Method | Path | Service | Description |
|--------|------|---------|-------------|
| POST | `/api/v1/analysis` | Orchestrator | Submit company analysis request |
| GET | `/api/v1/analysis/{id}/status` | Orchestrator | Get analysis status |
| GET | `/api/v1/analysis/{id}/report` | Orchestrator | Get analysis report |
| GET | `/api/v1/companies/{nip}` | Company Profile | Get company profile |
| GET | `/api/v1/financial/{nip}` | Financial Analysis | Get financial indicators |
| GET | `/api/v1/aml/{nip}` | AML/KYC | Get AML check result |
| GET | `/api/v1/sales/{nip}` | Sales Insight | Get sales opportunities |
| GET | `/api/v1/reports/{id}` | Report Generator | Get generated report |
| GET | `/api/v1/notifications` | Notification | Get advisor notifications |

---

## Three Versions

### V1 – External Only
- Uses only publicly available data (KRS, CRBR, news, LinkedIn)
- No internal bank data integration
- Generates One Pager and Full Report on demand

### V2 – Internal + External
- Adds internal bank data (credit history, portfolio, CRM)
- Enhanced financial analysis with pre-limit calculation
- Credit rating based on bank's internal scoring

### V3 – Proactive
- Scheduled monitoring of all portfolio companies
- Automatic detection of time-sensitive opportunities
- Proactive push notifications to advisors (CRM, Email, MS Teams)

---

## Technology Stack

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.3 |
| Architecture | Hexagonal (Ports & Adapters) |
| Event Streaming | Apache Kafka 3.6 |
| API Gateway | Spring Cloud Gateway |
| Caching | Redis 7 |
| Database | PostgreSQL 16 |
| LLM | Spring AI + LangChain4j (GPT-4 / Azure OpenAI) |
| Observability | Prometheus + Grafana |
| Tracing | Micrometer Tracing |
| Build | Maven 3.9 |
| Containers | Docker Compose |

---

## Security Notes

### SIB Zone Protection
All services within the SIB zone must NOT send raw company data outside the bank network.

### Data Sanitization
The `DataSanitizer` in `corpai-llm-gateway` ensures that before any data is sent to external LLM providers (OpenAI, Azure):
- Internal account numbers are redacted
- Internal advisor/CRM IDs are redacted
- PESEL numbers are redacted
- Only NIP/KRS numbers and company names are allowed

### Authentication
All API calls through the gateway require JWT tokens issued by the bank's identity provider (Keycloak or equivalent).

---

## Kafka Topics

| Topic | Producer | Consumer |
|-------|---------|---------|
| `corpai.analysis.requested` | Orchestrator | Company Profile, Financial, AML, Sales |
| `corpai.analysis.completed` | Orchestrator | Gateway/Clients |
| `corpai.sales.opportunity.detected` | Sales Insight | Notification |
| `corpai.report.generated` | Report Generator | Gateway/Clients |
| `corpai.aml.check.completed` | AML/KYC | Orchestrator |
