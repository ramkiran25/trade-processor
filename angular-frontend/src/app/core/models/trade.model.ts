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