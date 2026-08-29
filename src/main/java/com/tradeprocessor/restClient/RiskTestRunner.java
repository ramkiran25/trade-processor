package com.tradeprocessor.restClient;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.PriceDto;
import com.tradeprocessor.domain.RiskAssessmentDto;
import com.tradeprocessor.domain.TradeDto;
import com.tradeprocessor.service.RiskAssessmentService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RiskTestRunner implements CommandLineRunner {
  private final RiskAssessmentService riskService;

  @Override
  public void run(String... args) throws Exception {
    TradeDto trade = new TradeDto("TRD-9901", "AAPL", new BigDecimal("150"),
        new PriceDto(new BigDecimal("185.50"), "USD"), "EQUITY");

    RiskAssessmentDto assessment = riskService.evaluateTrade(trade);

    System.out.println("\n==========================================");
    System.out.println("   FASTAPI RISK ASSESSMENT RESULTS");
    System.out.println("==========================================");
    assessment.results()
        .forEach(res -> System.out.printf(" Calculator : %-15s | Score: %8.4f | Breached: %-5s | Level: %s%n",
            res.calculatorName(), res.score(), res.isBreached(), res.riskLevel()));
    System.out.println("\n\n\n\n");

  }
  

}
