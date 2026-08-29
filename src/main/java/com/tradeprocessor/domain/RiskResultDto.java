package com.tradeprocessor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RiskResultDto(@JsonProperty("calculator_name") String calculatorName, Double score,
    @JsonProperty("is_breached") Boolean isBreached, @JsonProperty("risk_level") String riskLevel) {

}
