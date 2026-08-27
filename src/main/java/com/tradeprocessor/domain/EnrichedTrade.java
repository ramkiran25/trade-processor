package com.tradeprocessor.domain;

public record EnrichedTrade(Trade trade, InstrumentDetails instrumentDetails) {
}
