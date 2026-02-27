# CorpAI Frontend

React 19 + TypeScript + Vite + Tailwind CSS UI for the CorpAI Banking Intelligence Platform.

## Development

```bash
npm install
npm run dev   # http://localhost:3001 (proxies /api → gateway at :8079)
```

## Production Build

```bash
npm run build
```

The `dist/` output is served by the multi-stage Docker image defined in `Dockerfile`.

## Pages

| Route | Description |
|-------|-------------|
| `/` | Dashboard – feature overview and system version info |
| `/analysis/new` | Submit new company analysis (NIP, modules, report type) |
| `/analysis/:correlationId` | Live status + full report viewer (auto-polls every 3 s) |
| `/notifications` | Advisor proactive notifications |

