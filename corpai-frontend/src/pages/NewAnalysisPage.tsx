import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { submitAnalysis } from '../services/api';
import type { AnalysisModule, AppVersion, ReportType } from '../types';

const ALL_MODULES: { value: AnalysisModule; label: string }[] = [
  { value: 'COMPANY_PROFILE', label: 'Company Profile' },
  { value: 'FINANCIAL_ANALYSIS', label: 'Financial Analysis' },
  { value: 'AML_KYC', label: 'AML / KYC' },
  { value: 'SALES_INSIGHT', label: 'Sales Insight' },
  { value: 'ESG', label: 'ESG' },
  { value: 'ECOSYSTEM', label: 'Ecosystem' },
];

export default function NewAnalysisPage() {
  const navigate = useNavigate();
  const [nip, setNip] = useState('');
  const [krs, setKrs] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [reportType, setReportType] = useState<ReportType>('ONE_PAGER');
  const [appVersion, setAppVersion] = useState<AppVersion>('V1_EXTERNAL_ONLY');
  const [modules, setModules] = useState<AnalysisModule[]>([
    'COMPANY_PROFILE',
    'FINANCIAL_ANALYSIS',
    'AML_KYC',
    'SALES_INSIGHT',
  ]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function toggleModule(m: AnalysisModule) {
    setModules((prev) =>
      prev.includes(m) ? prev.filter((x) => x !== m) : [...prev, m],
    );
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!nip.trim()) {
      setError('NIP number is required.');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const result = await submitAnalysis({
        nip: nip.trim(),
        krs: krs.trim() || undefined,
        companyName: companyName.trim() || undefined,
        reportType,
        appVersion,
        modules,
      });
      navigate(`/analysis/${result.correlationId}`);
    } catch (err: unknown) {
      const msg =
        err instanceof Error ? err.message : 'Failed to submit analysis. Is the backend running?';
      setError(msg);
    } finally {
      setLoading(false);
    }
  }

  const inputClass =
    'w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500';
  const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">New Company Analysis</h1>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Company identifiers */}
        <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm space-y-4">
          <h2 className="font-semibold text-gray-700">Company Identifiers</h2>

          <div>
            <label htmlFor="nip" className={labelClass}>
              NIP <span className="text-red-500">*</span>
            </label>
            <input
              id="nip"
              type="text"
              className={inputClass}
              placeholder="e.g. 1234567890"
              value={nip}
              onChange={(e) => setNip(e.target.value)}
              maxLength={10}
            />
          </div>

          <div>
            <label htmlFor="krs" className={labelClass}>
              KRS (optional)
            </label>
            <input
              id="krs"
              type="text"
              className={inputClass}
              placeholder="e.g. 0000123456"
              value={krs}
              onChange={(e) => setKrs(e.target.value)}
            />
          </div>

          <div>
            <label htmlFor="companyName" className={labelClass}>
              Company Name (optional)
            </label>
            <input
              id="companyName"
              type="text"
              className={inputClass}
              placeholder="e.g. PKO Bank Polski S.A."
              value={companyName}
              onChange={(e) => setCompanyName(e.target.value)}
            />
          </div>
        </div>

        {/* Report settings */}
        <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm space-y-4">
          <h2 className="font-semibold text-gray-700">Report Settings</h2>

          <div>
            <label htmlFor="reportType" className={labelClass}>
              Report Type
            </label>
            <select
              id="reportType"
              className={inputClass}
              value={reportType}
              onChange={(e) => setReportType(e.target.value as ReportType)}
            >
              <option value="ONE_PAGER">One Pager</option>
              <option value="FULL_REPORT">Full Report</option>
              <option value="BOTH">Both</option>
            </select>
          </div>

          <div>
            <label htmlFor="appVersion" className={labelClass}>
              System Version
            </label>
            <select
              id="appVersion"
              className={inputClass}
              value={appVersion}
              onChange={(e) => setAppVersion(e.target.value as AppVersion)}
            >
              <option value="V1_EXTERNAL_ONLY">V1 — External Only (public data)</option>
              <option value="V2_INTERNAL_EXTERNAL">
                V2 — Internal + External (requires bank data access)
              </option>
              <option value="V3_PROACTIVE">V3 — Proactive Monitoring</option>
            </select>
          </div>
        </div>

        {/* Modules */}
        <div className="bg-white border border-gray-200 rounded-xl p-6 shadow-sm">
          <h2 className="font-semibold text-gray-700 mb-3">Analysis Modules</h2>
          <div className="grid grid-cols-2 gap-2">
            {ALL_MODULES.map(({ value, label }) => (
              <label
                key={value}
                className="flex items-center gap-2 text-sm cursor-pointer select-none"
              >
                <input
                  type="checkbox"
                  checked={modules.includes(value)}
                  onChange={() => toggleModule(value)}
                  className="w-4 h-4 accent-blue-600"
                />
                {label}
              </label>
            ))}
          </div>
        </div>

        {/* Error */}
        {error && (
          <div className="bg-red-50 border border-red-300 text-red-700 rounded-lg px-4 py-3 text-sm">
            {error}
          </div>
        )}

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-700 hover:bg-blue-600 disabled:bg-blue-300 text-white font-semibold py-3 rounded-xl transition-colors"
        >
          {loading ? 'Submitting…' : 'Submit Analysis'}
        </button>
      </form>
    </div>
  );
}
