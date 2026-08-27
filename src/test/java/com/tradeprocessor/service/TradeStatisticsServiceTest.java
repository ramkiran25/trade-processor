package com.tradeprocessor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tradeprocessor.domain.Statistics;
import com.tradeprocessor.entity.TradeEntity;
import com.tradeprocessor.repository.TradeRepository;

/**
 * Assumes TradeEntity exposes getQuantity() and getPriceAmount(), matching how
 * TradeStatisticsService and ZScoreCalculator already call it.
 */
@ExtendWith(MockitoExtension.class)
class TradeStatisticsServiceTest {

  @Mock private TradeRepository tradeRepository;
  @Mock private TradeEntity trade1;
  @Mock private TradeEntity trade2;
  @Mock private TradeEntity trade3;

  @Test
  void computesMeanAndStdDeviationAcrossActualDeviationFromMean() {
    // Notionals: 10, 20, 30 -> mean = 20
    // Variance = ((10-20)^2 + (20-20)^2 + (30-20)^2) / 3 = (100 + 0 + 100) / 3 = 66.667
    // StdDev = sqrt(66.667) ~= 8.165
    when(trade1.getQuantity()).thenReturn(BigDecimal.ONE);
    when(trade1.getPriceAmount()).thenReturn(BigDecimal.TEN);

    when(trade2.getQuantity()).thenReturn(BigDecimal.ONE);
    when(trade2.getPriceAmount()).thenReturn(BigDecimal.valueOf(20));

    when(trade3.getQuantity()).thenReturn(BigDecimal.ONE);
    when(trade3.getPriceAmount()).thenReturn(BigDecimal.valueOf(30));

    when(tradeRepository.findAll()).thenReturn(List.of(trade1, trade2, trade3));

    TradeStatisticsService service = new TradeStatisticsService(tradeRepository);
    Statistics stats = service.calculateStatistics();

    assertEquals(20.0, stats.getMean(), 0.0001);
    assertEquals(8.1649, stats.getStdDeviation(), 0.001);
  }

  @Test
  void returnsZeroMeanAndStdDeviationWhenNoTradesExist() {
    when(tradeRepository.findAll()).thenReturn(List.of());

    TradeStatisticsService service = new TradeStatisticsService(tradeRepository);
    Statistics stats = service.calculateStatistics();

    assertEquals(0.0, stats.getMean());
    assertEquals(0.0, stats.getStdDeviation());
  }

  @Test
  void returnsZeroStdDeviationWhenAllNotionalsAreIdentical() {
    when(trade1.getQuantity()).thenReturn(BigDecimal.ONE);
    when(trade1.getPriceAmount()).thenReturn(BigDecimal.valueOf(50));

    when(trade2.getQuantity()).thenReturn(BigDecimal.ONE);
    when(trade2.getPriceAmount()).thenReturn(BigDecimal.valueOf(50));

    when(tradeRepository.findAll()).thenReturn(List.of(trade1, trade2));

    TradeStatisticsService service = new TradeStatisticsService(tradeRepository);
    Statistics stats = service.calculateStatistics();

    assertEquals(50.0, stats.getMean(), 0.0001);
    assertEquals(0.0, stats.getStdDeviation(), 0.0001);
  }
}
