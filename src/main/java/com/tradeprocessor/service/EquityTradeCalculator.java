package com.tradeprocessor.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.InstrumentType;
import com.tradeprocessor.domain.Trade;

@Component
public class EquityTradeCalculator implements TradeCalculator {

  @Override
  public boolean supports(InstrumentType type) {

    return type == InstrumentType.EQUITY;
  }

  @Override
  public TradeCalculation calculate(EnrichedTrade enrichedTrade) {

    Trade trade = enrichedTrade.trade();
    BigDecimal notional = trade.getQuantity().multiply(trade.getPrice().amount());
    return new TradeCalculation(notional, InstrumentType.EQUITY);
  }

  @Override
  public String notional() {
    return "EQUITY_NOTIONAL";
  }

}
