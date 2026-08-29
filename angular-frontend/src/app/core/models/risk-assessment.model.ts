export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';

export interface RiskResult {
  calculator_name: string;
  score: number;
  is_breached: boolean;
  risk_level: RiskLevel;
}

export interface RiskAssessment {
  tradeId?: string;
  timestamp?: string;
  results: RiskResult[];
}

export interface OverallRiskStatus {
  label: string;
  cssClass: 'status-success' | 'status-warning' | 'status-danger' | 'status-neutral';
}