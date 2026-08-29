package com.tradeprocessor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.tradeprocessor.domain.RiskAssessmentDto;
import com.tradeprocessor.domain.TradeDto;

@Service
public class RiskAssessmentService {
  private final RestClient riskEngineClient;

  public RiskAssessmentService(RestClient riskEngineClient) {
    this.riskEngineClient = riskEngineClient;
  }

  public RiskAssessmentDto evaluateTrade(TradeDto trade) {
    return riskEngineClient.post().uri("/api/v1/risk/assess").body(trade).retrieve()
        .body(RiskAssessmentDto.class);
  }
}
