package com.tradeprocessor.service;

import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.InstrumentType;

public interface TradeCalculator {
  boolean supports(InstrumentType type);

  TradeCalculation calculate(EnrichedTrade trade);

  String  notional();
}
