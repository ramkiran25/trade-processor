package com.tradeprocessor.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
  public Money {
    Objects.requireNonNull(amount);
    Objects.requireNonNull(currency);
    
  }
}
