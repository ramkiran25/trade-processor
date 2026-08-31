package com.tradeprocessor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.tradeprocessor.domain.RiskAssessmentDto;
import com.tradeprocessor.domain.TradeDto;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class RiskAssessmentService {
  private final RestClient riskEngineClient;

  public RiskAssessmentService(RestClient riskEngineClient) {
    this.riskEngineClient = riskEngineClient;
  }
  
  public RiskAssessmentDto evaluateTrade(TradeDto trade) {
    log.info(" <<<<   Call to python analytics MS ");
    return riskEngineClient.post().uri("/api/v1/risk/assess").body(trade).retrieve()
        .body(RiskAssessmentDto.class);
  }
}
