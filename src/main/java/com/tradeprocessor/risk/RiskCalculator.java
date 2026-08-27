package com.tradeprocessor.risk;

import com.tradeprocessor.domain.RiskResult;
import com.tradeprocessor.domain.Trade;

public interface RiskCalculator {
  RiskResult  calculate(Trade trade);
}
