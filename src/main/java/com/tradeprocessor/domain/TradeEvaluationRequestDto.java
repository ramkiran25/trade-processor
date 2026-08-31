package com.tradeprocessor.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeEvaluationRequestDto(String symbol, BigDecimal quantity, BigDecimal price,
    String assetClass,
    // Optional asset-class specific metadata
    String currencyPair, String maturityDate, BigDecimal couponRate) {

  public TradeDto toTradeDto() {
    String effectiveSymbol =
        "FX".equals(assetClass) && currencyPair != null ? currencyPair : symbol;
    return new TradeDto("TRD-" + System.currentTimeMillis(), effectiveSymbol, quantity,
        new PriceDto(price, "USD"), assetClass);
  }

  /**
   * Builds the domain Trade needed by TradeProcessor.process(). Requires a placeholder
   * InstrumentDetails up front, since InstrumentEnricher (which normally produces
   * InstrumentDetails) hasn't run yet at this point.
   */
  public Trade toTradeDomain() {
    String effectiveSymbol =
        "FX".equals(assetClass) && currencyPair != null ? currencyPair : symbol;

    InstrumentType instrumentType = mapToInstrumentType(assetClass);

    return new Trade("TRD-" + System.currentTimeMillis(), quantity, new Money(price, "USD"),
        new InstrumentDetails(instrumentType), LocalDateTime.now(), TradeStatus.RECEIVED,
        effectiveSymbol);
  }

  private InstrumentType mapToInstrumentType(String assetClass) {
    if (assetClass == null) {
      return InstrumentType.EQUITY;
    }
    return switch (assetClass.toUpperCase()) {
      case "EQUITY" -> InstrumentType.EQUITY;
      case "FX" -> InstrumentType.FX;
      case "FIXED_INCOME" -> InstrumentType.FIXED_INCOME;
      case "COMMODITY" -> InstrumentType.DERIVATIVE; 
      default -> InstrumentType.EQUITY;
    };
  }
}
