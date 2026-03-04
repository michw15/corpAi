# 🔐 CorpAI V1 — Warstwy Bezpieczeństwa

> Dokument przeznaczony dla Departamentu Bezpieczeństwa PKO BP.  
> Opisuje mechanizmy ochrony danych i przepływ informacji w wersji **V1 (External Only)**.

---

## Przepływ żądania — diagram sekwencji

```
Doradca          Gateway            Orchestrator      Agenty (SIB)       Data Collector     LLM Gateway       GPT-4/Azure
   │                │                    │                 │                    │                 │                 │
   │──── HTTPS ────►│                    │                 │                    │                 │                 │
   │  POST /api/    │                    │                 │                    │                 │                 │
   │  v1/analysis   │                    │                 │                    │                 │                 │
   │  {NIP: ...}    │                    │                 │                    │                 │                 │
   │                │                    │                 │                    │                 │                 │
   │           [1] Weryfikacja JWT       │                 │                    │                 │                 │
   │           + Rate Limiting           │                 │                    │                 │                 │
   │                │                    │                 │                    │                 │                 │
   │                │────── Kafka ───────►                 │                    │                 │                 │
   │                │  analysis.requested│                 │                    │                 │                 │
   │                │                    │──── Kafka ─────►│                    │                 │                 │
   │                │                    │  (równolegle)   │                    │                 │                 │
   │                │                    │             [2] Zbieranie danych     │                 │                 │
   │                │                    │                 │────── HTTPS ──────►│                 │                 │
   │                │                    │                 │                    │── KRS/CRBR ────►│                 │
   │                │                    │                 │◄──────────────────│                 │                 │
   │                │                    │             [3] Sanityzacja         │                 │                 │
   │                │                    │                 │──────────────────────────────────── ►│                 │
   │                │                    │                 │                    │            sanitize()            │
   │                │                    │                 │                    │            isSafeToTransmit()    │
   │                │                    │                 │                    │                 │────── HTTPS ───►│
   │                │                    │                 │                    │                 │  TYLKO NIP/KRS  │
   │                │                    │                 │                    │                 │◄────────────────│
   │                │                    │             [4] Generowanie raportu │                 │                 │
   │                │                    │◄──── Kafka ─────│                    │                 │                 │
   │                │◄──────────────────│                 │                    │                 │                 │
   │◄───────────────│                    │                 │                    │                 │                 │
   │  Raport gotowy │                    │                 │                    │                 │                 │
```

---

## Strefy bezpieczeństwa

```
╔══════════════════════════════════════════════════════════════╗
║                   🏦 STREFA SIB (bank)                      ║
║                                                              ║
║   Doradca → Gateway → Orchestrator → Agenty analityczne     ║
║                                    → LLM Gateway            ║
║                                    → Baza danych (Postgres) ║
║                                    → Cache (Redis)          ║
║                                    → Kolejki (Kafka)        ║
╠══════════════════════════════════════════════════════════════╣
║              🌐 POZA SIB (wyłącznie do odczytu)             ║
║                                                              ║
║   Data Collector → KRS API (gov.pl)                         ║
║                 → CRBR API (podatki.gov.pl)                 ║
║                 → EMIS, portale przetargowe, LinkedIn       ║
║   LLM Gateway  → GPT-4 / Azure OpenAI                       ║
║                  ⚠️  TYLKO po sanityzacji danych!           ║
╚══════════════════════════════════════════════════════════════╝
```

> **V1 nie dotyka żadnych wewnętrznych danych bankowych.**  
> Żadne rachunki, historia kredytowa, dane klientów — wyłącznie publiczne rejestry państwowe.

---

## Warstwa 1 — Dostęp do systemu

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│                         │  │                         │  │                         │
│  🔑  JWT Authentication │  │  🚦  Rate Limiting      │  │  🚪  Single Entry Point │
│                         │  │                         │  │                         │
│  Każde żądanie wymaga   │  │  Ochrona przed          │  │  Żaden serwis nie jest  │
│  ważnego tokenu JWT     │  │  brute-force i          │  │  bezpośrednio dostępny  │
│  wystawionego przez     │  │  nadużyciem API         │  │  z zewnątrz — tylko     │
│  Keycloak / IdP banku   │  │                         │  │  przez Gateway          │
│                         │  │                         │  │                         │
│  [Spring Security       │  │  [Spring Cloud          │  │  [Spring Cloud          │
│   OAuth2 Resource       │  │   Gateway Filter]       │  │   Gateway :8079]        │
│   Server]               │  │                         │  │                         │
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

---

## Warstwa 2 — Komunikacja wewnętrzna

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│                         │  │                         │  │                         │
│  📨  Async Messaging    │  │  🔒  Brak HTTP między   │  │  📋  Audit Trail        │
│                         │  │      serwisami          │  │                         │
│  Serwisy komunikują     │  │                         │  │  Każde zdarzenie        │
│  się wyłącznie przez    │  │  Kompromitacja jednego  │  │  zapisane w topiku      │
│  kolejki Kafka —        │  │  serwisu nie daje       │  │  Kafka — pełna          │
│  nie przez HTTP         │  │  dostępu do innych      │  │  historia operacji      │
│                         │  │                         │  │                         │
│  [Apache Kafka 3.6]     │  │  [Izolacja sieciowa     │  │  [Kafka Topics          │
│                         │  │   SIB Zone]             │  │   + retention log]      │
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

---

## Warstwa 3 — Ochrona danych przed AI

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│                         │  │                         │  │                         │
│  🧹  DataSanitizer      │  │  ✅  isSafeToTransmit() │  │  ⛔  Fail-Closed        │
│                         │  │                         │  │                         │
│  Przed wysłaniem do     │  │  Podwójna weryfikacja   │  │  Jeśli dane nie         │
│  GPT-4 automatycznie    │  │  po sanityzacji —       │  │  przejdą filtra →       │
│  redaguje:              │  │  jeśli cokolwiek        │  │  SecurityException      │
│  · numery NRB           │  │  zostało →              │  │  dane fizycznie         │
│  · PESEL                │  │  blokada                │  │  nie wychodzą           │
│  · ID doradców/CRM      │  │                         │  │                         │
│                         │  │  [DataSanitizer         │  │  [LlmGatewayService     │
│  [regex pattern match]  │  │  .isSafeToTransmit()]   │  │  .analyzeCompany()]     │
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

### Jak działa sanityzacja — przykład

```
DANE WEJŚCIOWE (surowe z KRS/CRBR):
──────────────────────────────────────────────────────────────
Firma: ACME Sp. z o.o., NIP: 1234567890
Właściciel: Jan Kowalski, PESEL: 85010112345
Nr rachunku: 12345678901234567890123456
Doradca bankowy: ADV-KOWAL-001
Komentarz z CRM: CRM-2024-XYZ
──────────────────────────────────────────────────────────────

         ▼ sanitize() ▼

DANE PO SANITYZACJI:
──────────────────────────────────────────────────────────────
Firma: ACME Sp. z o.o., NIP: 1234567890
Właściciel: Jan Kowalski, PESEL: [PESEL_REDACTED]
Nr rachunku: [ACCOUNT_REDACTED]
Doradca bankowy: [INTERNAL_ID_REDACTED]
Komentarz z CRM: [INTERNAL_ID_REDACTED]
──────────────────────────────────────────────────────────────

         ▼ isSafeToTransmit() = TRUE ▼

         GPT-4 widzi TYLKO: NIP + nazwa firmy
```

---

## Warstwa 4 — Przechowywanie danych

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│                         │  │                         │  │                         │
│  🗄️  Baza danych        │  │  ⚡  Cache              │  │  🕐  TTL na dane        │
│                         │  │                         │  │                         │
│  Wszystkie wyniki       │  │  Odpowiedzi LLM         │  │  Dane w cache           │
│  analiz i raporty       │  │  cachowane w Redis —    │  │  automatycznie          │
│  wyłącznie wewnątrz     │  │  te same zapytania      │  │  wygasają po 4h —       │
│  strefy SIB             │  │  nie trafiają           │  │  brak zbędnego          │
│                         │  │  ponownie do GPT-4      │  │  przetrzymywania        │
│  [PostgreSQL 16         │  │                         │  │                         │
│   inside SIB]           │  │  [Redis 7]              │  │  [Redis TTL: 4h]        │
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

---

## Warstwa 5 — Obserwowalność

```
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│                         │  │                         │  │                         │
│  🔍  Distributed        │  │  📊  Metryki            │  │  🚨  Alerty             │
│      Tracing            │  │      w czasie           │  │                         │
│                         │  │      rzeczywistym       │  │                         │
│  Każde żądanie ma       │  │                         │  │  Wizualne dashboardy    │
│  unikalny trace ID —    │  │  Każdy serwis           │  │  z progami alarmowymi   │
│  pełna ścieżka          │  │  eksportuje metryki     │  │  na anomalie            │
│  przez system           │  │  wydajności             │  │  i błędy               │
│  audytowalna            │  │  i błędów               │  │                         │
│                         │  │                         │  │                         │
│  [Micrometer Tracing]   │  │  [Prometheus]           │  │  [Grafana]              │
└─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

---

## Podsumowanie — 5 gwarancji bezpieczeństwa V1

```
┌─────────────────────────────────────────────────────────────────┐
│  1. ZERO wewnętrznych danych bankowych                         │
│     V1 operuje wyłącznie na danych z publicznych rejestrów     │
│     (KRS, CRBR) i internetu. Żaden rachunek, żaden klient.    │
├─────────────────────────────────────────────────────────────────┤
│  2. Jeden kontrolowany punkt wejścia                           │
│     Wszystko przez Gateway z JWT. Żaden serwis nie jest        │
│     bezpośrednio dostępny z zewnątrz.                          │
├─────────────────────────────────────────────────────────────────┤
│  3. Dane do AI przechodzą przez obligatoryjny filtr           │
│     DataSanitizer z podwójną weryfikacją — sanitize()          │
│     + isSafeToTransmit(). Failure → SecurityException.         │
���─────────────────────────────────────────────────────────────────┤
│  4. Serwisy wewnętrzne nie rozmawiają przez HTTP               │
│     Kafka jako broker — brak bezpośrednich wywołań,            │
│     brak lateral movement w razie kompromitacji serwisu.       │
├─────────────────────────────────────────────────────────────────┤
│  5. Pełny audit trail                                          │
│     Każde żądanie ma trace ID (Micrometer), metryki            │
│     w Prometheus, wizualizacja w Grafana.                      │
└─────────────────────────────────────────────────────────────────┘
```

---

*Dokument wygenerowany na podstawie kodu źródłowego repozytorium `michw15/corpAi` · V1 External Only · 2026-03-04*