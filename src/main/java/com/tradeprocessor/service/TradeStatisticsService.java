package com.tradeprocessor.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.tradeprocessor.domain.Statistics;
import com.tradeprocessor.repository.TradeRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TradeStatisticsService {
  private final TradeRepository tradeRepository;

  public Statistics calculateStatistics() {
    List<Double> nationals = tradeRepository.findAll().stream()
        .map(t -> t.getQuantity().multiply(t.getPriceAmount()).doubleValue()).toList();
    double mean = nationals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    double varience = nationals.stream().mapToDouble(n -> Math.pow(n - mean, 2)).average().orElse(0);
    double stdDeviation = Math.sqrt(varience);
    return new Statistics(mean, stdDeviation);
  }
}
