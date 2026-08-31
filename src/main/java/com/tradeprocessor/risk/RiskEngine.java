package com.tradeprocessor.risk;

import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.RiskAssessmentDto;

public interface RiskEngine {
  RiskAssessmentDto assessTrade(EnrichedTrade enrichedTrade);
}
