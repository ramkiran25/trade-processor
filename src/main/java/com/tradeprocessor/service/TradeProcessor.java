package com.tradeprocessor.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.RiskAssessmentDto;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.domain.TradeStatus;
import com.tradeprocessor.mapper.TradeEntityMapper;
import com.tradeprocessor.repository.TradeRepository;
import com.tradeprocessor.risk.RiskEngine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class TradeProcessor {
  private final TradeValidator tradeValidator;
  private final InstrumentEnricher enricher;
  private final TradeCalculatorFactory tradeCalculatorFactory;
  private final TradeRepository tradeRepository;

  private final RiskEngine riskEngine;

  public TradeProcessingResult process(Trade trade) {
    //Step 1  Validate the Trade
    tradeValidator.validateTrade(trade);
    
    //Step 2  Enrich the Trade
    EnrichedTrade enrichedTrade = enricher.enrich(trade);
    
    //Step 3. Perform assessment
    RiskAssessmentDto riskAssessmentDto = riskEngine.assessTrade(enrichedTrade);

    if (riskAssessmentDto.anomalyDetected()) {
      trade.markRejected();
      tradeRepository.save(TradeEntityMapper.toEntity(trade));
      return new TradeProcessingResult(trade.getTradeId(), TradeStatus.REJECTED, BigDecimal.ZERO,
          riskAssessmentDto);
    }

    TradeCalculator calculator =
        tradeCalculatorFactory.getCalculator(enrichedTrade.instrumentDetails().instrumentType());

    TradeCalculation calculation = calculator.calculate(enrichedTrade);
    trade.markProcessed();

    tradeRepository.save(TradeEntityMapper.toEntity(trade));
    return new TradeProcessingResult(trade.getTradeId(), TradeStatus.PROCESSED,
        calculation.notional(), riskAssessmentDto);
  }
}
