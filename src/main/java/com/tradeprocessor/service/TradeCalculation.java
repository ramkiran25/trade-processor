package com.tradeprocessor.service;

import java.math.BigDecimal;
import com.tradeprocessor.domain.InstrumentType;

public record TradeCalculation(BigDecimal notional, InstrumentType instrumentType) {

}
