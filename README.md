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
| `corpai-frontend` | 3001 | React web UI – advisor dashboard, analysis forms, report viewer |
| `corpai-common` | – | Shared domain model and Kafka events |

---

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **Node.js 20+** (for frontend development)
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

### 3a. Start the React Frontend (development)
```bash
cd corpai-frontend
npm install
npm run dev
# Opens at http://localhost:3001 with hot-reload
# API calls are proxied to the gateway at http://localhost:8079
```

### 3b. Build & Run Frontend via Docker Compose
```bash
docker compose up -d corpai-frontend
# Accessible at http://localhost:3001
```

### 4. Access Services
- **React UI**: http://localhost:3001
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
| **Frontend** | **React 19 + TypeScript + Vite + Tailwind CSS** |

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

---

## Infrastructure Design Decisions

This section explains **why** each infrastructure component is necessary and how the system achieves full asynchronous processing.

---

### Why Apache Kafka?

A corporate company analysis is a **long-running, multi-step workflow** that touches many independent services (KRS API, CRBR, financial analysis, AML/KYC, LLM, report generation). The total latency can exceed 30–60 seconds. Using synchronous HTTP calls between microservices for such a workflow creates several critical problems:

| Problem (sync HTTP) | Solution (Kafka) |
|---|---|
| Blocking HTTP thread for 60 s while downstream services process | Fire-and-forget event publish; HTTP thread is freed immediately |
| If one downstream service crashes, the whole chain fails | Kafka retains the event; the service reprocesses it after recovery |
| Adding a new agent (e.g. ESG scoring) requires changing the orchestrator code | New consumer subscribes to the existing topic with zero orchestrator changes |
| Only one consumer can receive a request | Fan-out: one `AnalysisRequestedEvent` is consumed **simultaneously** by Company Profile, Financial Analysis, AML/KYC, and Sales Insight agents |

#### How Kafka enables parallel agent execution

When an advisor requests analysis for a company, the Orchestrator publishes a **single** `AnalysisRequestedEvent` to the `corpai.analysis.requested` topic. All four downstream agents are **consumer groups** on that topic — they each receive the same event and run **in parallel**, without any agent waiting for another:

```
AnalysisRequestedEvent (NIP: 1234567890)
        │
        ├──► CompanyProfileAgent   ─── fetches KRS/CRBR, caches in Redis
        ├──► FinancialAnalysisAgent ─── calculates ratios, credit maturities
        ├──► AmlKycAgent           ─── performs ownership graph analysis
        └──► SalesInsightAgent     ─── scores opportunities (FX, leasing, ESG…)
```

Without Kafka this parallelism would require the Orchestrator to spawn threads and manage their lifecycle, handle partial failures, and implement retries manually — all solved for free by Kafka's consumer-group model.

#### Correlation ID and message ordering

Every event carries a `correlationId` (UUID generated at analysis request time). This ID is used as the **Kafka partition key**, which guarantees that all events belonging to the same analysis are processed in order within a partition. The `AnalysisStateManager` uses the `correlationId` to match incoming partial results and detect when all agents have finished.

#### Durability and replay

Kafka persists events on disk with configurable retention. If the Report Generator service is temporarily down, it will automatically consume the pending `ReportGeneratedEvent` when it restarts — no data is lost and no manual re-trigger is needed.

---

### Why Redis?

Redis serves **three distinct roles** in the platform, each addressing a different scalability or reliability concern:

#### 1. Analysis state management (Orchestrator)

The Orchestrator publishes one event and then waits for **four independent agents** to complete. It must track which agents have finished and aggregate their partial results. This aggregation state is stored in Redis with a 24-hour TTL:

```
Key:   corpai:analysis:{correlationId}
Value: { status, companyProfile, financialData, amlResult, salesOpportunities }
```

Storing this in-process (in JVM memory) would make the Orchestrator stateful and impossible to scale horizontally. Redis makes state externalized and shared across any number of Orchestrator replicas.

#### 2. Company profile caching (Company Profile Agent)

Each full analysis requires fetching data from the KRS API and CRBR API. These external APIs are rate-limited and have non-trivial latency (~1–3 seconds per call). For the same NIP, the data changes at most once a day.

```
Key:   corpai:company:{nip}
Value: serialized Company object
TTL:   24 hours
```

Without this cache, every analysis — including V3 proactive re-scans of the entire portfolio — would hammer external APIs. With Redis, repeated analyses of the same company within 24 hours hit the cache at < 1 ms, reducing both latency and external API costs.

#### 3. LLM response caching (LLM Gateway)

GPT-4 API calls are expensive (cost per token) and slow (1–10 seconds). For identical or structurally similar prompts — e.g. generating a One Pager for the same company twice in one day — the LLM Gateway caches responses:

```
Key:   corpai:llm:{sha256(sanitized_prompt)}
Value: LLM response text
TTL:   4 hours
```

The cache key is a SHA-256 hash of the **sanitized** prompt, so two advisors asking about the same company receive the cached response immediately without an extra LLM call.

---

### Asynchronous Event Flow — Step by Step

The following diagram shows the complete lifecycle of a company analysis request, from advisor click to report delivery, fully asynchronous:

```
  ADVISOR
    │
    │  POST /api/v1/analysis  { nip: "1234567890" }
    ▼
  GATEWAY (8079)
    │  JWT validation, rate limiting
    ▼
  ORCHESTRATOR (8080)
    │  1. Creates AnalysisRequest record in PostgreSQL (status=PENDING)
    │  2. Initialises state in Redis: corpai:analysis:{correlationId}
    │  3. Publishes AnalysisRequestedEvent → Kafka topic "corpai.analysis.requested"
    │  4. Returns HTTP 202 Accepted + { analysisId, correlationId }
    │
    │         ◄── HTTP response already returned to advisor ──►
    │
    │  (All steps below happen asynchronously, in parallel)
    │
    ├──► COMPANY PROFILE AGENT (8081)
    │      @KafkaListener("corpai.analysis.requested")
    │      - Redis cache hit? → return cached Company
    │      - Cache miss? → call KRS API + CRBR API → cache result (24h TTL)
    │      - Publishes partial result back to Orchestrator via Redis state update
    │
    ├──► FINANCIAL ANALYSIS AGENT (8082)
    │      @KafkaListener("corpai.analysis.requested")
    │      - Calculates financial ratios, credit maturities, FX exposure
    │      - Publishes partial result → updates Redis state
    │
    ├──► AML/KYC AGENT (8083)
    │      @KafkaListener("corpai.analysis.requested")
    │      - Builds ownership graph, checks PEP/sanctions lists
    │      - Publishes AmlCheckCompletedEvent → "corpai.aml.check.completed"
    │
    └──► SALES INSIGHT ENGINE (8084)
           @KafkaListener("corpai.analysis.requested")
           - Scores opportunities: leasing, ESG, FX hedging, credit renewal
           - If high-score opportunity found:
               → publishes SalesOpportunityDetectedEvent
                    └──► NOTIFICATION SERVICE (8089) sends push to advisor
           - Publishes partial result → updates Redis state
    │
    ▼
  ORCHESTRATOR (8080)
    │  Monitors Redis state for { correlationId }
    │  When all 4 agents have reported:
    │    - Updates PostgreSQL record (status=AGGREGATING)
    │    - Publishes AnalysisCompletedEvent → "corpai.analysis.completed"
    │
    ▼
  REPORT GENERATOR (8085)
    │  @KafkaListener("corpai.analysis.completed")
    │  - Calls LLM Gateway (8087) with sanitized data → GPT-4 generates narrative
    │  - LLM Gateway checks Redis cache first (4h TTL on SHA-256 prompt hash)
    │  - Renders HTML One Pager + Full Report
    │  - Publishes ReportGeneratedEvent → "corpai.report.generated"
    │  - Updates PostgreSQL: stores report HTML
    │
    ▼
  ADVISOR polls GET /api/v1/analysis/{id}/status  →  status=COMPLETED
             GET /api/v1/analysis/{id}/report     →  HTML report delivered
```

The advisor receives an immediate HTTP 202 response and can poll for status. The entire analysis pipeline runs asynchronously without blocking any HTTP thread, with full parallelism across the four analysis agents.

---

### Why PostgreSQL?

PostgreSQL is the **system of record** for all analysis requests and generated reports. It stores:
- Audit trail: who requested what and when
- Final report HTML (for retrieval after the async pipeline completes)
- Status transitions (PENDING → AGGREGATING → COMPLETED / FAILED)

PostgreSQL handles the **persistent, durable** data that must survive restarts, while Redis handles the **ephemeral, high-speed** state needed only during processing.

---

### Why Elasticsearch?

Elasticsearch enables full-text search across generated reports and detected sales opportunities. Advisors can search by company name, NIP, opportunity type, or any text within the AI-generated narrative — queries that are impractical on PostgreSQL without heavy indexing.

---

### Why Prometheus + Grafana?

Each microservice exposes a `/actuator/prometheus` endpoint (via Micrometer). Prometheus scrapes these metrics every 15 seconds. Grafana dashboards provide real-time visibility into:
- Kafka consumer lag per topic (detects a stuck agent before advisors notice)
- Redis cache hit/miss rates (measures caching efficiency)
- LLM Gateway latency and retry counts (monitors GPT-4 cost and reliability)
- Analysis pipeline end-to-end duration (P50/P95/P99 percentiles)
