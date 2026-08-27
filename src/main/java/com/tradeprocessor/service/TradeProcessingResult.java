package com.tradeprocessor.service;

import java.math.BigDecimal;
import com.tradeprocessor.domain.TradeStatus;


public record TradeProcessingResult(String tradeId, TradeStatus tradeStatus, BigDecimal notional) {
}
