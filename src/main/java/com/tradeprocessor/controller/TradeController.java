package com.tradeprocessor.controller;

import com.tradeprocessor.domain.TradeEvaluationRequestDto;
import com.tradeprocessor.service.TradeProcessingResult;
import com.tradeprocessor.service.TradeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/risk")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TradeController {

  private final TradeProcessor tradeProcessor;

  @PostMapping("/evaluate")
  public ResponseEntity<TradeProcessingResult> evaluateTrade(
      @RequestBody TradeEvaluationRequestDto request) {
    log.info(">>> Received request from Angular for symbol: {}", request.symbol());

    var trade = request.toTradeDomain();

    TradeProcessingResult result = tradeProcessor.process(trade);
    log.info("Result from  ML code:", result);
    return ResponseEntity.ok(result);
  }
}
