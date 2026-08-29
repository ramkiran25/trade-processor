package com.tradeprocessor.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TradeDto(
    @JsonProperty("trade_id")
    String tradeId, String symbol, BigDecimal quantity, PriceDto price,
    @JsonProperty("asset_class")
    String assetClass) {

}
