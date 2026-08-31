package com.tradeprocessor.service;

import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.InstrumentDetails;
import com.tradeprocessor.domain.Trade;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class InstrumentEnricher {
  private final InstrumentService instrumentService;

  public EnrichedTrade enrich(Trade trade) {
    InstrumentDetails instrumentDetails = instrumentService.getInstrument(trade.getSymbol());
    return new EnrichedTrade(trade,instrumentDetails);
  }
}
