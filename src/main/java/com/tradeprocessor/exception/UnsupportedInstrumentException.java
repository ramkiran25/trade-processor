package com.tradeprocessor.exception;

public class UnsupportedInstrumentException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UnsupportedInstrumentException(String msg) {
    super(msg);
  }

}
