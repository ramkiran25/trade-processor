package com.tradeprocessor.risk;

import org.springframework.stereotype.Service;
import com.tradeprocessor.domain.RiskLevel;
import com.tradeprocessor.domain.RiskResult;
import com.tradeprocessor.domain.Statistics;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.service.TradeStatisticsService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ZScoreCalculator implements RiskCalculator {
  private final TradeStatisticsService tradeStatisticsService;

  @Override
  public RiskResult calculate(Trade trade) {
    Statistics statistics = tradeStatisticsService.calculateStatistics();
    if (statistics.getStdDeviation() == 0) {
      return new RiskResult(0.0, false, RiskLevel.LOW);
    }
    double tradeNotational = trade.getQuantity().multiply(trade.getPrice().amount()).doubleValue();
    double zScore = (tradeNotational - statistics.getMean()) / statistics.getStdDeviation();
    return new RiskResult(zScore, Math.abs(zScore) > 3, determineRisk(zScore));
  }

  private RiskLevel determineRisk(double zScore) {
    if (Math.abs(zScore) > 3) {
      return RiskLevel.HIGH;
    }
    if (Math.abs(zScore) > 2) {
      return RiskLevel.MEDIUM;
    }
    return RiskLevel.LOW;
  }

}
