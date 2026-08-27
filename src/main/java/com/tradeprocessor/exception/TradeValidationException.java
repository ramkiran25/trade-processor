package com.tradeprocessor.exception;

public class TradeValidationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public TradeValidationException(String msg) {
    super(msg);
  }
}
