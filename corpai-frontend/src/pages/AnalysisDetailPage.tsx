import { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getAnalysisStatus, getAnalysisReport } from '../services/api';
import type { AggregatedAnalysis, AnalysisStatus } from '../types';
import { CheckCircleIcon, ExclamationTriangleIcon, ArrowPathIcon } from '../components/Icons';

const POLL_INTERVAL_MS = 3000;

function StatusBadge({ status }: { status: AnalysisStatus }) {
  const styles: Record<AnalysisStatus, string> = {
    IN_PROGRESS: 'bg-blue-100 text-blue-700',
    COMPLETED: 'bg-green-100 text-green-700',
    PARTIAL: 'bg-yellow-100 text-yellow-700',
    FAILED: 'bg-red-100 text-red-700',
  };
  return (
    <span className={`text-xs font-semibold px-3 py-1 rounded-full ${styles[status]}`}>
      {status.replace('_', ' ')}
    </span>
  );
}

function formatCurrency(value?: number) {
  if (value == null) return '—';
  return new Intl.NumberFormat('pl-PL', {
    style: 'currency',
    currency: 'PLN',
    notation: 'compact',
    maximumFractionDigits: 1,
  }).format(value);
}

function formatPct(value?: number) {
  if (value == null) return '—';
  return `${(value * 100).toFixed(1)} %`;
}

function SectionCard({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
      <h3 className="font-semibold text-gray-800 mb-4 border-b border-gray-100 pb-2">{title}</h3>
      {children}
    </div>
  );
}

function Row({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="flex justify-between text-sm py-1 border-b border-gray-50 last:border-0">
      <span className="text-gray-500">{label}</span>
      <span className="font-medium text-gray-800">{value ?? '—'}</span>
    </div>
  );
}

export default function AnalysisDetailPage() {
  const { correlationId } = useParams<{ correlationId: string }>();
  const [status, setStatus] = useState<AnalysisStatus>('IN_PROGRESS');
  const [report, setReport] = useState<AggregatedAnalysis | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchStatus = useCallback(async () => {
    if (!correlationId) return;
    try {
      const s = await getAnalysisStatus(correlationId);
      setStatus(s.status);
      if (s.status === 'COMPLETED' || s.status === 'PARTIAL') {
        const r = await getAnalysisReport(correlationId);
        setReport(r);
      }
    } catch {
      setError('Failed to load analysis. Make sure the backend services are running.');
    }
  }, [correlationId]);

  useEffect(() => {
    fetchStatus();
    const interval = setInterval(() => {
      if (status === 'IN_PROGRESS') fetchStatus();
    }, POLL_INTERVAL_MS);
    return () => clearInterval(interval);
  }, [fetchStatus, status]);

  if (error) {
    return (
      <div className="max-w-2xl mx-auto mt-10 bg-red-50 border border-red-200 rounded-xl p-6 text-red-700 text-sm">
        <ExclamationTriangleIcon className="w-6 h-6 mb-2" />
        {error}
        <div className="mt-4">
          <Link to="/analysis/new" className="text-blue-600 underline">
            ← New Analysis
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Analysis Report</h1>
          <p className="text-xs text-gray-400 mt-1 font-mono">{correlationId}</p>
        </div>
        <div className="flex items-center gap-3">
          <StatusBadge status={status} />
          {status === 'IN_PROGRESS' && (
            <ArrowPathIcon className="w-5 h-5 text-blue-500 animate-spin" />
          )}
        </div>
      </div>

      {status === 'IN_PROGRESS' && !report && (
        <div className="bg-blue-50 border border-blue-200 rounded-xl p-6 text-center text-blue-700 text-sm">
          <ArrowPathIcon className="w-8 h-8 mx-auto mb-3 animate-spin" />
          Analysis is in progress. This page refreshes automatically every {POLL_INTERVAL_MS / 1000} seconds.
        </div>
      )}

      {/* Company Profile */}
      {report?.company && (
        <SectionCard title="Company Profile">
          <Row label="Full Name" value={report.company.fullName} />
          <Row label="NIP" value={report.company.nip} />
          <Row label="KRS" value={report.company.krs} />
          <Row label="Legal Form" value={report.company.legalForm} />
          <Row label="Sector" value={report.company.sectorName ?? report.company.sectorPkd} />
          <Row label="City" value={report.company.city} />
          <Row label="Status" value={report.company.status} />
          <Row label="Employees" value={report.company.employeeCount} />
          {report.company.boardMembers && report.company.boardMembers.length > 0 && (
            <div className="mt-3">
              <p className="text-xs text-gray-500 mb-1 font-medium">Board Members</p>
              <ul className="text-sm space-y-1">
                {report.company.boardMembers.map((m, i) => (
                  <li key={i} className="text-gray-700">
                    {m.fullName} — <span className="text-gray-500 text-xs">{m.role}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </SectionCard>
      )}

      {/* AML / KYC */}
      {report?.amlCheckResult && (
        <SectionCard title="AML / KYC Check">
          <div className="flex items-center gap-3 mb-4">
            {report.amlCheckResult.goNoGo === 'GO' ? (
              <span className="flex items-center gap-1 bg-green-100 text-green-700 font-bold px-4 py-1 rounded-full text-sm">
                <CheckCircleIcon className="w-4 h-4" /> GO
              </span>
            ) : (
              <span className="flex items-center gap-1 bg-red-100 text-red-700 font-bold px-4 py-1 rounded-full text-sm">
                <ExclamationTriangleIcon className="w-4 h-4" />{' '}
                {report.amlCheckResult.goNoGo}
              </span>
            )}
          </div>
          <Row label="Red Flags" value={report.amlCheckResult.redFlagsCount} />
          <Row
            label="Ownership Clear"
            value={report.amlCheckResult.ownershipChainClear ? 'Yes' : 'No'}
          />
          <Row
            label="PEP Exposure"
            value={report.amlCheckResult.pepExposure ? 'Yes ⚠️' : 'No'}
          />
          <Row
            label="Sanction Hit"
            value={report.amlCheckResult.sanctionHit ? 'Yes 🚨' : 'No'}
          />
          {report.amlCheckResult.summary && (
            <p className="mt-3 text-sm text-gray-600 italic">{report.amlCheckResult.summary}</p>
          )}
        </SectionCard>
      )}

      {/* Financial Indicators */}
      {report?.financialIndicators && report.financialIndicators.length > 0 && (
        <SectionCard title="Financial Indicators">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 border-b border-gray-100">
                  <th className="pb-2">Year</th>
                  <th className="pb-2">Revenue</th>
                  <th className="pb-2">EBITDA</th>
                  <th className="pb-2">Net Profit</th>
                  <th className="pb-2">Net Margin</th>
                  <th className="pb-2">Revenue YoY</th>
                  <th className="pb-2">D/E Ratio</th>
                </tr>
              </thead>
              <tbody>
                {report.financialIndicators.map((fi) => (
                  <tr key={fi.year} className="border-b border-gray-50 hover:bg-gray-50">
                    <td className="py-2 font-semibold">{fi.year}</td>
                    <td>{formatCurrency(fi.revenuePln)}</td>
                    <td>{formatCurrency(fi.ebitda)}</td>
                    <td>{formatCurrency(fi.netProfit)}</td>
                    <td>{formatPct(fi.netProfitMargin)}</td>
                    <td>{formatPct(fi.revenueGrowthYoY)}</td>
                    <td>{fi.debtToEquity?.toFixed(2) ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Credit maturities */}
          {report.financialIndicators.some(
            (fi) => fi.upcomingCreditMaturities && fi.upcomingCreditMaturities.length > 0,
          ) && (
            <div className="mt-4">
              <p className="text-xs font-semibold text-gray-600 mb-2">Upcoming Credit Maturities</p>
              {report.financialIndicators.flatMap((fi) =>
                (fi.upcomingCreditMaturities ?? []).map((cm, i) => (
                  <div
                    key={i}
                    className={`flex items-center justify-between text-xs py-1.5 border-b border-gray-50 ${
                      cm.isUrgent ? 'text-red-700' : 'text-gray-700'
                    }`}
                  >
                    <span>
                      {cm.bankName} · {cm.creditType}
                    </span>
                    <span>
                      {formatCurrency(cm.amount)} · {cm.maturityDate} · {cm.daysToMaturity}d
                      {cm.isUrgent && ' 🚨'}
                    </span>
                  </div>
                )),
              )}
            </div>
          )}
        </SectionCard>
      )}

      {/* Sales Opportunities */}
      {report?.salesOpportunities && report.salesOpportunities.length > 0 && (
        <SectionCard title="Sales Opportunities">
          <div className="space-y-3">
            {report.salesOpportunities.map((opp) => {
              const priorityColors: Record<string, string> = {
                CRITICAL: 'bg-red-100 text-red-700',
                HIGH: 'bg-orange-100 text-orange-700',
                MEDIUM: 'bg-yellow-100 text-yellow-700',
                LOW: 'bg-gray-100 text-gray-600',
              };
              return (
                <div
                  key={opp.id}
                  className="border border-gray-100 rounded-lg p-4 hover:shadow-sm transition-shadow"
                >
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <span className="font-semibold text-gray-800 text-sm">
                        {opp.type.replace(/_/g, ' ')}
                      </span>
                      <span
                        className={`ml-2 text-xs px-2 py-0.5 rounded-full font-semibold ${
                          priorityColors[opp.priority]
                        }`}
                      >
                        {opp.priority}
                      </span>
                    </div>
                    <span className="text-xs text-gray-400">
                      {(opp.confidenceScore * 100).toFixed(0)}% confidence
                    </span>
                  </div>
                  <p className="text-sm text-gray-600 mt-1">{opp.description}</p>
                  {opp.recommendedAction && (
                    <p className="text-xs text-blue-700 mt-1">
                      💡 {opp.recommendedAction}
                    </p>
                  )}
                  {opp.estimatedRevenuePotential && (
                    <p className="text-xs text-green-700 mt-1">
                      Est. revenue: {formatCurrency(opp.estimatedRevenuePotential)}
                    </p>
                  )}
                </div>
              );
            })}
          </div>
        </SectionCard>
      )}

      {/* ESG */}
      {report?.esgReport && (
        <SectionCard title="ESG Report">
          <Row label="ESG Score" value={report.esgReport.esgScore} />
          <Row label="Environment" value={report.esgReport.environmentScore} />
          <Row label="Social" value={report.esgReport.socialScore} />
          <Row label="Governance" value={report.esgReport.governanceScore} />
          {report.esgReport.summary && (
            <p className="text-sm text-gray-600 mt-3 italic">{report.esgReport.summary}</p>
          )}
        </SectionCard>
      )}

      {status === 'COMPLETED' && !report && (
        <div className="text-center text-gray-500 text-sm py-10">
          Analysis completed. Report data not available.
        </div>
      )}

      <div className="text-center">
        <Link to="/analysis/new" className="text-sm text-blue-600 hover:underline">
          ← Run Another Analysis
        </Link>
      </div>
    </div>
  );
}
