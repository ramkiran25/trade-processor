package com.tradeprocessor.service;

import org.springframework.stereotype.Service;
import com.tradeprocessor.domain.InstrumentDetails;
import com.tradeprocessor.domain.InstrumentType;

@Service
public class DefaultInstrumentService implements InstrumentService {

  @Override
  public InstrumentDetails getInstrument(String instrumentId) {
    // TODO Auto-generated method stub
    return new InstrumentDetails(InstrumentType.EQUITY);
  }

}
