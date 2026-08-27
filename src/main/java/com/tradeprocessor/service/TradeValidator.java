package com.tradeprocessor.service;

import com.tradeprocessor.domain.Trade;

public interface TradeValidator {
  void validateTrade(Trade trade);
}
