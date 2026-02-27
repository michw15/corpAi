import axios from 'axios';
import type {
  AnalysisRequest,
  AggregatedAnalysis,
  AnalysisStatusResponse,
  Notification,
} from '../types';

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

// ---- Analysis ----

export async function submitAnalysis(
  request: AnalysisRequest,
): Promise<{ correlationId: string; status: string }> {
  const { data } = await api.post('/analysis', request);
  return data;
}

export async function getAnalysisStatus(
  correlationId: string,
): Promise<AnalysisStatusResponse> {
  const { data } = await api.get(`/analysis/${correlationId}/status`);
  return data;
}

export async function getAnalysisReport(
  correlationId: string,
): Promise<AggregatedAnalysis> {
  const { data } = await api.get(`/analysis/${correlationId}/report`);
  return data;
}

// ---- Notifications ----

export async function getNotifications(): Promise<Notification[]> {
  const { data } = await api.get('/notifications');
  return data;
}
