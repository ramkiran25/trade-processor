package com.tradeprocessor.domain;

import java.math.BigDecimal;

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
}
