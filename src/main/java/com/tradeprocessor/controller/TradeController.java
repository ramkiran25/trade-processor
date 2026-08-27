package com.tradeprocessor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.service.TradeProcessingResult;
import com.tradeprocessor.service.TradeProcessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/trade")
public class TradeController {
  private final TradeProcessor tradeProcessor;

  @PostMapping("/create")
  public ResponseEntity<TradeProcessingResult> createTrade(@RequestBody Trade trade) {

    TradeProcessingResult result = tradeProcessor.process(trade);
    return ResponseEntity.ok(result);
  }

}
