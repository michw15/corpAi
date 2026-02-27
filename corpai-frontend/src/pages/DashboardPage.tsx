import { Link } from 'react-router-dom';
import { MagnifyingGlassIcon, ChartBarIcon, BellIcon, CheckCircleIcon } from '../components/Icons';

const features = [
  {
    icon: <MagnifyingGlassIcon className="w-8 h-8 text-blue-600" />,
    title: 'Company Analysis',
    description:
      'Analyze any company by NIP or KRS number. Get AML/KYC check, financial indicators and sales opportunity scoring in minutes.',
    link: '/analysis/new',
    linkLabel: 'Start Analysis',
  },
  {
    icon: <ChartBarIcon className="w-8 h-8 text-green-600" />,
    title: 'Sales Intelligence',
    description:
      'Detect credit maturities, FX exposure, leasing opportunities and 12 other sales signals automatically ranked by revenue potential.',
    link: '/analysis/new',
    linkLabel: 'New Report',
  },
  {
    icon: <BellIcon className="w-8 h-8 text-yellow-600" />,
    title: 'Proactive Notifications',
    description:
      'Receive alerts when time-sensitive opportunities arise in your portfolio. Never miss a refinancing window again.',
    link: '/notifications',
    linkLabel: 'View Alerts',
  },
  {
    icon: <CheckCircleIcon className="w-8 h-8 text-red-600" />,
    title: 'AML / KYC',
    description:
      'Automated Go / No-Go decision with red-flag detection, ownership graph analysis, PEP screening and sanctions check.',
    link: '/analysis/new',
    linkLabel: 'Run Check',
  },
];

const modules = [
  { label: 'Company Profile', description: 'KRS · CRBR · Board · Shareholders' },
  { label: 'Financial Analysis', description: 'Revenue · EBITDA · Debt · Liquidity' },
  { label: 'AML / KYC', description: 'Red flags · PEP · Sanctions' },
  { label: 'Sales Insight', description: '12 opportunity types scored & ranked' },
  { label: 'ESG', description: 'Environmental · Social · Governance' },
  { label: 'Ecosystem', description: 'Subsidiaries · Partners · Competitors' },
];

export default function DashboardPage() {
  return (
    <div className="space-y-10">
      {/* Hero */}
      <section className="bg-gradient-to-br from-blue-800 to-blue-600 rounded-2xl p-10 text-white shadow-lg">
        <h1 className="text-4xl font-bold mb-3">
          Welcome to <span className="text-yellow-300">CorpAI</span>
        </h1>
        <p className="text-blue-100 text-lg max-w-2xl mb-6">
          AI-powered corporate banking sales intelligence for PKO BP advisors.
          Analyze companies, detect opportunities and prepare for client meetings
          — all in one place.
        </p>
        <Link
          to="/analysis/new"
          className="inline-flex items-center gap-2 bg-yellow-400 hover:bg-yellow-300 text-blue-900 font-semibold px-6 py-3 rounded-xl transition-colors text-sm"
        >
          <MagnifyingGlassIcon className="w-4 h-4" />
          Start New Analysis
        </Link>
      </section>

      {/* Feature cards */}
      <section>
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Key Capabilities</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {features.map((f) => (
            <div
              key={f.title}
              className="bg-white rounded-xl border border-gray-200 p-5 shadow-sm flex flex-col gap-3"
            >
              {f.icon}
              <h3 className="font-semibold text-gray-800">{f.title}</h3>
              <p className="text-sm text-gray-500 flex-1">{f.description}</p>
              <Link
                to={f.link}
                className="text-sm font-medium text-blue-700 hover:underline mt-auto"
              >
                {f.linkLabel} →
              </Link>
            </div>
          ))}
        </div>
      </section>

      {/* Analysis modules */}
      <section>
        <h2 className="text-xl font-semibold text-gray-800 mb-4">
          Analysis Modules
        </h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          {modules.map((m) => (
            <div
              key={m.label}
              className="bg-white border border-gray-200 rounded-xl p-4 shadow-sm"
            >
              <p className="font-semibold text-gray-800 text-sm">{m.label}</p>
              <p className="text-xs text-gray-500 mt-1">{m.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Architecture overview */}
      <section className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-gray-800 mb-3">System Versions</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
          <div className="border border-blue-200 bg-blue-50 rounded-lg p-4">
            <span className="font-bold text-blue-700">V1 — External Only</span>
            <p className="text-gray-600 mt-1">
              Public KRS, CRBR, news, LinkedIn. On-demand One Pager & Full Report.
            </p>
          </div>
          <div className="border border-green-200 bg-green-50 rounded-lg p-4">
            <span className="font-bold text-green-700">V2 — Internal + External</span>
            <p className="text-gray-600 mt-1">
              Adds bank internal data, credit history, CRM portfolio, pre-limit calculation.
            </p>
          </div>
          <div className="border border-yellow-200 bg-yellow-50 rounded-lg p-4">
            <span className="font-bold text-yellow-700">V3 — Proactive</span>
            <p className="text-gray-600 mt-1">
              Scheduled portfolio monitoring with push notifications to advisors via CRM / Teams.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
