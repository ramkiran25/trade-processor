package com.tradeprocessor.domain;

public record RiskResult(double zScore,boolean anamolyDetected,RiskLevel riskLevel) {

}
