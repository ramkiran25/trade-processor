package com.tradeprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Statistics {
  private double mean;
  private double stdDeviation;
}
