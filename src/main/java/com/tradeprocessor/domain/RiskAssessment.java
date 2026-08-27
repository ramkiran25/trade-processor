package com.tradeprocessor.domain;

import java.util.List;

public record RiskAssessment(List<RiskResult> results) {
  public boolean anamolyDetected() {
    return results.stream().anyMatch(RiskResult::anamolyDetected);
  }

  public RiskLevel highestRiskLevel() {
    if (results.stream().anyMatch(r -> r.riskLevel() == RiskLevel.HIGH)) {
      return RiskLevel.HIGH;
    }
    if (results.stream().anyMatch(r -> r.riskLevel() == RiskLevel.MEDIUM)) {
      return RiskLevel.MEDIUM;
    }
    return RiskLevel.LOW;
  }
}
