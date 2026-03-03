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

## Decyzje architektoniczne — infrastruktura

Ta sekcja wyjaśnia **dlaczego** każdy komponent infrastruktury jest niezbędny oraz w jaki sposób system osiąga pełne przetwarzanie asynchroniczne.

---

### Dlaczego Apache Kafka?

Analiza spółki korporacyjnej to **długotrwały, wieloetapowy przepływ pracy**, który angażuje wiele niezależnych serwisów (KRS API, CRBR, analiza finansowa, AML/KYC, LLM, generowanie raportu). Łączne opóźnienie może przekroczyć 30–60 sekund. Stosowanie synchronicznych wywołań HTTP między mikroserwisami w takim przepływie rodzi kilka krytycznych problemów:

| Problem (synchroniczne HTTP) | Rozwiązanie (Kafka) |
|---|---|
| Blokowanie wątku HTTP przez 60 s podczas przetwarzania przez serwisy downstream | Publikacja zdarzenia „wyślij i zapomnij" — wątek HTTP zostaje natychmiast zwolniony |
| Awaria jednego serwisu downstream powoduje przerwanie całego łańcucha | Kafka zachowuje zdarzenie; serwis przetwarza je ponownie po odzyskaniu sprawności |
| Dodanie nowego agenta (np. scoringu ESG) wymaga zmiany kodu Orchestratora | Nowy konsument subskrybuje istniejący temat bez jakichkolwiek zmian w Orchestratorze |
| Tylko jeden konsument może odebrać żądanie | Fan-out: jedno `AnalysisRequestedEvent` jest konsumowane **równocześnie** przez agentów: Company Profile, Financial Analysis, AML/KYC oraz Sales Insight |

#### Jak Kafka umożliwia równoległe wykonywanie agentów

Gdy doradca zleca analizę spółki, Orchestrator publikuje **jedno** zdarzenie `AnalysisRequestedEvent` w topiku `corpai.analysis.requested`. Wszyscy czterej agenci downstream są **oddzielnymi grupami konsumentów** tego topiku — każdy odbiera to samo zdarzenie i działa **równolegle**, bez czekania na pozostałych:

```
AnalysisRequestedEvent (NIP: 1234567890)
        │
        ├──► CompanyProfileAgent   ─── pobiera dane KRS/CRBR, cachuje w Redis
        ├──► FinancialAnalysisAgent ─── oblicza wskaźniki, terminy kredytów
        ├──► AmlKycAgent           ─── analizuje graf właścicielski
        └──► SalesInsightAgent     ─── scoruje szanse sprzedażowe (FX, leasing, ESG…)
```

Bez Kafki uzyskanie takiej równoległości wymagałoby od Orchestratora ręcznego zarządzania wątkami, obsługi częściowych awarii i implementacji mechanizmu retry — to wszystko Kafka rozwiązuje automatycznie dzięki modelowi grup konsumentów.

#### Correlation ID i kolejność wiadomości

Każde zdarzenie zawiera `correlationId` (UUID generowany w momencie złożenia zlecenia analizy). ID ten jest używany jako **klucz partycji Kafki**, co gwarantuje, że wszystkie zdarzenia należące do tej samej analizy są przetwarzane w kolejności wewnątrz jednej partycji. `AnalysisStateManager` używa `correlationId` do dopasowywania napływających częściowych wyników i wykrywania momentu, gdy wszyscy agenci zakończyli pracę.

#### Trwałość i odtwarzanie zdarzeń

Kafka utrwala zdarzenia na dysku z konfigurowalnym okresem retencji. Jeśli serwis Report Generator jest tymczasowo niedostępny, po restarcie automatycznie przetworzy oczekujące zdarzenie `ReportGeneratedEvent` — żadne dane nie są tracone i nie jest wymagane ręczne ponowne wyzwolenie analizy.

---

### Dlaczego Redis?

Redis pełni **trzy odrębne role** w platformie, każda adresuje inny problem skalowalności lub niezawodności:

#### 1. Zarządzanie stanem analizy (Orchestrator)

Orchestrator publikuje jedno zdarzenie, a następnie czeka na zakończenie pracy przez **czterech niezależnych agentów**. Musi śledzić, którzy agenci zakończyli działanie, i agregować ich częściowe wyniki. Ten stan agregacji jest przechowywany w Redis z TTL wynoszącym 24 godziny:

```
Klucz:   corpai:analysis:{correlationId}
Wartość: { status, companyProfile, financialData, amlResult, salesOpportunities }
```

Przechowywanie tego stanu w pamięci JVM uczyniłoby Orchestratora stanowym i uniemożliwiłoby skalowanie poziome. Redis eksternalizuje stan i udostępnia go dowolnej liczbie replik Orchestratora.

#### 2. Cache profilu spółki (Company Profile Agent)

Każda pełna analiza wymaga pobrania danych z KRS API i CRBR API. Te zewnętrzne API mają limity wywołań i nietrywialną latencję (~1–3 sekundy na jedno wywołanie). Dla danego NIP dane zmieniają się co najwyżej raz dziennie.

```
Klucz:  corpai:company:{nip}
Wartość: zserializowany obiekt Company
TTL:    24 godziny
```

Bez tego cache każda analiza — w tym proaktywne re-skany całego portfela w wersji V3 — przeciążałaby zewnętrzne API. Dzięki Redis powtórne analizy tej samej spółki w ciągu 24 godzin trafią w cache w czasie < 1 ms, co redukuje zarówno opóźnienia, jak i koszty wywołań zewnętrznych API.

#### 3. Cache odpowiedzi LLM (LLM Gateway)

Wywołania GPT-4 API są kosztowne (opłata za tokeny) i wolne (1–10 sekund). Dla identycznych lub strukturalnie podobnych promptów — np. generowanie One Pagera dla tej samej spółki dwa razy w ciągu jednego dnia — LLM Gateway cachuje odpowiedzi:

```
Klucz:  corpai:llm:{sha256(oczyszczony_prompt)}
Wartość: tekst odpowiedzi LLM
TTL:    4 godziny
```

Kluczem cache jest hash SHA-256 **oczyszczonego** prompta, dzięki czemu dwaj doradcy pytający o tę samą spółkę otrzymają odpowiedź z cache natychmiast, bez dodatkowego wywołania LLM.

---

### Asynchroniczny przepływ zdarzeń — krok po kroku

Poniższy diagram przedstawia pełny cykl życia zlecenia analizy spółki — od kliknięcia przez doradcę do dostarczenia raportu, w pełni asynchronicznie:

```
  DORADCA
    │
    │  POST /api/v1/analysis  { nip: "1234567890" }
    ▼
  GATEWAY (8079)
    │  Walidacja JWT, rate limiting
    ▼
  ORCHESTRATOR (8080)
    │  1. Tworzy rekord AnalysisRequest w PostgreSQL (status=PENDING)
    │  2. Inicjalizuje stan w Redis: corpai:analysis:{correlationId}
    │  3. Publikuje AnalysisRequestedEvent → temat Kafka "corpai.analysis.requested"
    │  4. Zwraca HTTP 202 Accepted + { analysisId, correlationId }
    │
    │         ◄── odpowiedź HTTP już zwrócona do doradcy ──►
    │
    │  (Wszystkie poniższe kroki wykonują się asynchronicznie, równolegle)
    │
    ├──► COMPANY PROFILE AGENT (8081)
    │      @KafkaListener("corpai.analysis.requested")
    │      - Trafienie w cache Redis? → zwróć zbuforowaną spółkę
    │      - Brak w cache? → wywołaj KRS API + CRBR API → zapisz wynik (TTL 24h)
    │      - Publikuje częściowy wynik do Orchestratora przez aktualizację stanu Redis
    │
    ├──► FINANCIAL ANALYSIS AGENT (8082)
    │      @KafkaListener("corpai.analysis.requested")
    │      - Oblicza wskaźniki finansowe, terminy kredytów, ekspozycję FX
    │      - Publikuje częściowy wynik → aktualizuje stan Redis
    │
    ├──► AML/KYC AGENT (8083)
    │      @KafkaListener("corpai.analysis.requested")
    │      - Buduje graf właścicielski, sprawdza listy PEP/sankcji
    │      - Publikuje AmlCheckCompletedEvent → "corpai.aml.check.completed"
    │
    └──► SALES INSIGHT ENGINE (8084)
           @KafkaListener("corpai.analysis.requested")
           - Scoruje szanse: leasing, ESG, hedging FX, odnowienie kredytu
           - Jeśli znaleziono szansę o wysokim score:
               → publikuje SalesOpportunityDetectedEvent
                    └──► NOTIFICATION SERVICE (8089) wysyła powiadomienie do doradcy
           - Publikuje częściowy wynik → aktualizuje stan Redis
    │
    ▼
  ORCHESTRATOR (8080)
    │  Monitoruje stan Redis dla { correlationId }
    │  Gdy wszyscy 4 agenci zgłoszą wyniki:
    │    - Aktualizuje rekord PostgreSQL (status=AGGREGATING)
    │    - Publikuje AnalysisCompletedEvent → "corpai.analysis.completed"
    │
    ▼
  REPORT GENERATOR (8085)
    │  @KafkaListener("corpai.analysis.completed")
    │  - Wywołuje LLM Gateway (8087) z oczyszczonymi danymi → GPT-4 generuje narrację
    │  - LLM Gateway sprawdza najpierw cache Redis (TTL 4h, klucz SHA-256 prompta)
    │  - Renderuje HTML One Pager + Pełny Raport
    │  - Publikuje ReportGeneratedEvent → "corpai.report.generated"
    │  - Aktualizuje PostgreSQL: zapisuje HTML raportu
    │
    ▼
  DORADCA odpytuje GET /api/v1/analysis/{id}/status  →  status=COMPLETED
                   GET /api/v1/analysis/{id}/report  →  raport HTML dostarczony
```

Doradca otrzymuje natychmiastową odpowiedź HTTP 202 i może odpytywać o status. Cały potok analizy działa asynchronicznie, nie blokując żadnego wątku HTTP, z pełną równoległością czterech agentów analitycznych.

---

### Dlaczego PostgreSQL?

PostgreSQL jest **systemem referencyjnym (source of truth)** dla wszystkich zleceń analizy i wygenerowanych raportów. Przechowuje:
- Ślad audytowy: kto, co i kiedy zlecił
- HTML finalnego raportu (do pobrania po zakończeniu asynchronicznego potoku)
- Przejścia statusów (PENDING → AGGREGATING → COMPLETED / FAILED)

PostgreSQL obsługuje dane **trwałe i niezawodne**, które muszą przeżyć restarty, podczas gdy Redis obsługuje **efemeryczny, wysokowydajny** stan potrzebny wyłącznie podczas przetwarzania.

---

### Dlaczego Elasticsearch?

Elasticsearch umożliwia pełnotekstowe przeszukiwanie wygenerowanych raportów i wykrytych szans sprzedażowych. Doradcy mogą wyszukiwać po nazwie spółki, NIP, typie szansy lub dowolnym fragmencie narracji wygenerowanej przez AI — zapytania tego rodzaju są niepraktyczne w PostgreSQL bez rozbudowanego indeksowania.

---

### Dlaczego Prometheus + Grafana?

Każdy mikroserwis udostępnia endpoint `/actuator/prometheus` (przez Micrometer). Prometheus zbiera metryki co 15 sekund. Dashboardy Grafany zapewniają wgląd w czasie rzeczywistym w:
- Opóźnienie konsumentów Kafki (consumer lag) per temat — wykrywa zablokowanego agenta zanim doradcy to zauważą
- Wskaźniki trafień/braków cache Redis — mierzy efektywność buforowania
- Opóźnienia LLM Gateway i liczby retry — monitoruje koszty i niezawodność GPT-4
- Czas trwania potoku analizy od końca do końca (percentyle P50/P95/P99)
