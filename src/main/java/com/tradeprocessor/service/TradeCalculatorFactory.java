package com.tradeprocessor.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.InstrumentType;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.exception.UnsupportedInstrumentException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class TradeCalculatorFactory {
  private final List<TradeCalculator> calculators;

  public TradeCalculator getCalculator(InstrumentType type) {
    return calculators.stream().filter(c -> c.supports(type)).findFirst()
        .orElseThrow(() -> new UnsupportedInstrumentException("Unsupported instrument"));
  }

  public TradeCalculation calculate(EnrichedTrade enrichedTrade) {

    Trade trade = enrichedTrade.trade();

    BigDecimal notional = trade.getQuantity().multiply(trade.getPrice().amount());

    return new TradeCalculation(notional, enrichedTrade.instrumentDetails().instrumentType());
  }
}
