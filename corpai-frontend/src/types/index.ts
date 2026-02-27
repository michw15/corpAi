export type ReportType = 'ONE_PAGER' | 'FULL_REPORT' | 'BOTH';

export type AppVersion =
  | 'V1_EXTERNAL_ONLY'
  | 'V2_INTERNAL_EXTERNAL'
  | 'V3_PROACTIVE';

export type AnalysisModule =
  | 'COMPANY_PROFILE'
  | 'FINANCIAL_ANALYSIS'
  | 'AML_KYC'
  | 'SALES_INSIGHT'
  | 'ESG'
  | 'ECOSYSTEM';

export type AnalysisStatus = 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | 'PARTIAL';

export interface AnalysisRequest {
  nip: string;
  krs?: string;
  companyName?: string;
  reportType: ReportType;
  appVersion: AppVersion;
  modules: AnalysisModule[];
}

export interface BoardMember {
  fullName: string;
  role: string;
  pesel?: string;
}

export interface Company {
  nip: string;
  krs?: string;
  regon?: string;
  fullName: string;
  shortName?: string;
  legalForm?: string;
  sectorPkd?: string;
  sectorName?: string;
  registeredAddress?: string;
  city?: string;
  voivodeship?: string;
  status?: string;
  employeeCount?: number;
  websiteUrl?: string;
  linkedInUrl?: string;
  boardMembers?: BoardMember[];
  subsidiaries?: string[];
  shareholders?: string[];
  foundedDate?: string;
  lastUpdated?: string;
}

export interface CreditMaturity {
  bankName: string;
  amount: number;
  maturityDate: string;
  creditType: string;
  daysToMaturity: number;
  isUrgent: boolean;
}

export interface FinancialIndicators {
  year: number;
  revenuePln?: number;
  ebitda?: number;
  netProfit?: number;
  netProfitMargin?: number;
  revenueGrowthYoY?: number;
  employeeCount?: number;
  currentRatio?: number;
  quickRatio?: number;
  debtToEquity?: number;
  longTermDebt?: number;
  shortTermDebt?: number;
  hasFxExposure?: boolean;
  fxDifferencesValue?: number;
  exportCountries?: string[];
  importCountries?: string[];
  exportRevenueSharePct?: number;
  hasLeasing?: boolean;
  leasingValue?: number;
  usesFactoring?: boolean;
  upcomingCreditMaturities?: CreditMaturity[];
}

export interface AmlCheckResult {
  goNoGo: 'GO' | 'NO_GO' | 'CONDITIONAL';
  redFlagsCount: number;
  redFlags?: string[];
  ownershipChainClear: boolean;
  pepExposure: boolean;
  sanctionHit: boolean;
  summary?: string;
}

export type SalesOpportunityPriority = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';

export interface SalesOpportunity {
  id: string;
  companyNip: string;
  type: string;
  priority: SalesOpportunityPriority;
  description: string;
  evidenceSource?: string;
  evidenceText?: string;
  actionDate?: string;
  estimatedRevenuePotential?: number;
  recommendedProduct?: string;
  recommendedAction?: string;
  confidenceScore: number;
}

export interface EsgReport {
  esgScore?: number;
  environmentScore?: number;
  socialScore?: number;
  governanceScore?: number;
  summary?: string;
}

export interface AggregatedAnalysis {
  correlationId: string;
  companyNip: string;
  analysisStartedAt?: string;
  analysisCompletedAt?: string;
  company?: Company;
  financialIndicators?: FinancialIndicators[];
  amlCheckResult?: AmlCheckResult;
  salesOpportunities?: SalesOpportunity[];
  esgReport?: EsgReport;
  status: AnalysisStatus;
}

export interface AnalysisStatusResponse {
  correlationId: string;
  status: AnalysisStatus;
  companyNip: string;
}

export interface Notification {
  id: string;
  advisorId: string;
  companyNip: string;
  companyName?: string;
  title: string;
  message: string;
  priority: SalesOpportunityPriority;
  createdAt: string;
  read: boolean;
}
