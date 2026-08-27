package com.tradeprocessor.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.InstrumentDetails;
import com.tradeprocessor.domain.InstrumentType;
import com.tradeprocessor.domain.Money;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.domain.TradeStatus;
import com.tradeprocessor.service.EquityTradeCalculator;
import com.tradeprocessor.service.TradeCalculation;

class EquityTradeCalculatorTest {

  private final EquityTradeCalculator calculator = new EquityTradeCalculator();

  @Test
  void supportsEquityInstrumentType() {
    assertTrue(calculator.supports(InstrumentType.EQUITY));
  }

  @Test
  void doesNotSupportNonEquityInstrumentTypes() {
    assertFalse(calculator.supports(InstrumentType.FX));
    assertFalse(calculator.supports(InstrumentType.BOND));
    assertFalse(calculator.supports(InstrumentType.DERIVATIVE));
  }

  @Test
  void calculatesNotionalAsQuantityTimesPrice() {
    InstrumentDetails details = new InstrumentDetails(InstrumentType.EQUITY);
    Money price = new Money(BigDecimal.valueOf(100), "EUR");
    Trade trade = new Trade("abc123", BigDecimal.valueOf(40), price, details,
        LocalDateTime.of(2026, 8, 25, 23, 7, 16), TradeStatus.RECEIVED);
    EnrichedTrade enrichedTrade = new EnrichedTrade(trade, details);

    TradeCalculation result = calculator.calculate(enrichedTrade);

    assertEquals(0, BigDecimal.valueOf(4000).compareTo(result.notional()));
    assertEquals(InstrumentType.EQUITY, result.instrumentType());
  }

  @Test
  void notionalLabelIsEquityNotional() {
    assertEquals("EQUITY_NOTIONAL", calculator.notional());
  }
}
