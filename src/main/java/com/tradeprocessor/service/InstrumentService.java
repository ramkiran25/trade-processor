package com.tradeprocessor.service;

import com.tradeprocessor.domain.InstrumentDetails;

public interface InstrumentService {
  InstrumentDetails getInstrument(String instrumentId);
}
