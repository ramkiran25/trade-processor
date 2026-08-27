package com.tradeprocessor.risk;

import com.tradeprocessor.domain.RiskAssessment;
import com.tradeprocessor.domain.Trade;

public interface RiskEngine {
   RiskAssessment assessTrade(Trade trade);
}
