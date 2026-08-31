export interface Price {
  amount: number;
  currency: 'USD' | 'EUR' | 'GBP' | 'PLN';
}

export interface Trade {
  tradeId: string;
  symbol: string;
  quantity: number;
  assetClass: 'EQUITY' | 'FX' | 'COMMODITY' | 'FIXED_INCOME';
  price: Price;
}
export interface RiskAssessmentDto {
  tradeId?: string;
  timestamp?: string;
  results: RiskResultDto[];
}
export interface RiskResultDto {
  calculator_name: string;
  score: number;
  is_breached: boolean;
  risk_level: 'LOW' | 'MEDIUM' | 'HIGH';
}
export interface TradeProcessingResult {
  tradeId: string;
  tradeStatus: string;
  notional: number;
  riskAssessmentDto: RiskAssessmentDto;
}
