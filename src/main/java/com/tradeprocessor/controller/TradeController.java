package com.tradeprocessor.controller;

import com.tradeprocessor.domain.RiskAssessmentDto;
import com.tradeprocessor.domain.TradeDto;
import com.tradeprocessor.domain.TradeEvaluationRequestDto;
import com.tradeprocessor.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/risk")
@CrossOrigin(origins = "http://localhost:4200") // Prevents CORS errors in dev
@RequiredArgsConstructor
public class TradeController {

    private final RiskAssessmentService riskService;

    @PostMapping("/evaluate")
    public ResponseEntity<RiskAssessmentDto> evaluateTrade(@RequestBody TradeEvaluationRequestDto request) {
        System.out.println(">>> Received request from Angular for symbol: " + request.symbol());
        
        // 1. Convert flat HTTP request -> domain TradeDto
        TradeDto trade = request.toTradeDto();
        
        // 2. Pass domain object to FastAPI calculation engine
        RiskAssessmentDto assessment = riskService.evaluateTrade(trade);
        
        // 3. Return results back to Angular
        return ResponseEntity.ok(assessment);
    }
}