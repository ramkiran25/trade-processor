package com.tradeprocessor.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tradeprocessor.domain.InstrumentDetails;
import com.tradeprocessor.domain.InstrumentType;
import com.tradeprocessor.domain.Money;
import com.tradeprocessor.domain.RiskLevel;
import com.tradeprocessor.domain.RiskResult;
import com.tradeprocessor.domain.Statistics;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.domain.TradeStatus;
import com.tradeprocessor.service.TradeStatisticsService;

/**
 * Assumes RiskResult is a record RiskResult(double zScore, boolean anomalyDetected,
 * RiskLevel riskLevel) - accessors zScore()/anomalyDetected()/riskLevel() with no get/is
 * prefix, matching RiskAssessment's confirmed record-style accessor in TradeProcessor.java.
 * If RiskResult is actually a plain class with getZScore()/isAnomalyDetected()/getRiskLevel(),
 * swap the accessor calls below accordingly.
 */
@ExtendWith(MockitoExtension.class)
class ZScoreCalculatorTest {

  @Mock private TradeStatisticsService tradeStatisticsService;

  private Trade tradeWithNotional(double quantity, double price) {
    InstrumentDetails details = new InstrumentDetails(InstrumentType.EQUITY);
    Money money = new Money(BigDecimal.valueOf(price), "EUR");
    return new Trade("t1", BigDecimal.valueOf(quantity), money, details,
        LocalDateTime.now(), TradeStatus.RECEIVED);
  }

  @Test
  void flagsHighRiskWhenZScoreExceedsThree() {
    when(tradeStatisticsService.calculateStatistics()).thenReturn(new Statistics(100.0, 10.0));

    ZScoreCalculator calculator = new ZScoreCalculator(tradeStatisticsService);
    // notional = 1 * 200 = 200 -> zScore = (200 - 100) / 10 = 10
    RiskResult result = calculator.calculate(tradeWithNotional(1, 200));

    assertEquals(10.0, result.zScore(), 0.0001);
    assertTrue(result.anamolyDetected());
    assertEquals(RiskLevel.HIGH, result.riskLevel());
  }

  @Test
  void flagsMediumRiskWhenZScoreBetweenTwoAndThree() {
    when(tradeStatisticsService.calculateStatistics()).thenReturn(new Statistics(100.0, 10.0));

    ZScoreCalculator calculator = new ZScoreCalculator(tradeStatisticsService);
    // notional = 1 * 125 = 125 -> zScore = 2.5
    RiskResult result = calculator.calculate(tradeWithNotional(1, 125));

    assertEquals(2.5, result.zScore(), 0.0001);
    assertFalse(result.anamolyDetected());
    assertEquals(RiskLevel.MEDIUM, result.riskLevel());
  }

  @Test
  void flagsLowRiskWhenZScoreWithinTwoStdDeviations() {
    when(tradeStatisticsService.calculateStatistics()).thenReturn(new Statistics(100.0, 10.0));

    ZScoreCalculator calculator = new ZScoreCalculator(tradeStatisticsService);
    // notional = 1 * 105 = 105 -> zScore = 0.5
    RiskResult result = calculator.calculate(tradeWithNotional(1, 105));

    assertEquals(0.5, result.zScore(), 0.0001);
    assertFalse(result.anamolyDetected());
    assertEquals(RiskLevel.LOW, result.riskLevel());
  }

  @Test
  void returnsLowRiskWithoutDivisionByZeroWhenStdDeviationIsZero() {
    when(tradeStatisticsService.calculateStatistics()).thenReturn(new Statistics(100.0, 0.0));

    ZScoreCalculator calculator = new ZScoreCalculator(tradeStatisticsService);
    RiskResult result = calculator.calculate(tradeWithNotional(1, 500));

    assertEquals(0.0, result.zScore(), 0.0001);
    assertFalse(result.anamolyDetected());
    assertEquals(RiskLevel.LOW, result.riskLevel());
  }
}
