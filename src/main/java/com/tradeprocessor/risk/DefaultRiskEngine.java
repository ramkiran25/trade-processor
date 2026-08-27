package com.tradeprocessor.risk;

import java.util.List;
import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.RiskAssessment;
import com.tradeprocessor.domain.RiskResult;
import com.tradeprocessor.domain.Trade;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DefaultRiskEngine implements RiskEngine {

  private final List<RiskCalculator> riskCalculators;

  @Override
  public RiskAssessment assessTrade(Trade trade) {
    List<RiskResult> results =
        riskCalculators.stream().map(calculator -> calculator.calculate(trade)).toList();

    return new RiskAssessment(results);
  }
}
