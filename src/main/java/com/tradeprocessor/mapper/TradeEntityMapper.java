package com.tradeprocessor.mapper;

import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.entity.TradeEntity;

public class TradeEntityMapper {
  public static TradeEntity toEntity(Trade trade) {
    return new TradeEntity(trade.getTradeId(), trade.getQuantity(), trade.getPrice().amount(),
        trade.getPrice().currency(), trade.getInstrumentDetails().instrumentType().name(),
        trade.getTradeTime(), trade.getStatus());
  }
}
