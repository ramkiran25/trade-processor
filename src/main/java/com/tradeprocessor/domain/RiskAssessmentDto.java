package com.tradeprocessor.domain;

import java.util.List;

public record RiskAssessmentDto(List<RiskResultDto> results) {
  public boolean anomalyDetected() {
    return results.stream().anyMatch(r -> Boolean.TRUE.equals(r.isBreached()));
  }

  public String highestRiskLevel() {
    if (results.stream().anyMatch(r -> "HIGH".equals(r.riskLevel()))) {
      return "HIGH";
    }
    if (results.stream().anyMatch(r -> "MEDIUM".equals(r.riskLevel()))) {
      return "MEDIUM";
    }
    return "LOW";
  }
}
