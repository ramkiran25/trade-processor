package com.tradeprocessor.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Trade {
  private String tradeId;
  private BigDecimal quantity;
  private Money price;
  private InstrumentDetails instrumentDetails;
  private LocalDateTime tradeTime;
  private TradeStatus status;
  private String symbol;
  public void markProcessed() {
    this.status = TradeStatus.PROCESSED;
  }

  public void markValidated() {
    this.status = TradeStatus.VALIDATED;
  }

  public void markRejected() {
    this.status = TradeStatus.REJECTED;
  }

//  public Trade(String tradeId2, BigDecimal quantity2, Money price2,
//      InstrumentDetails instrumentDetails2, LocalDateTime tradeTime2, String name) {
//    // TODO Auto-generated constructor stub
//  }
}


