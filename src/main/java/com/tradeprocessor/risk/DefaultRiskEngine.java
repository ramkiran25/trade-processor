package com.tradeprocessor.risk;

import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.EnrichedTrade;
import com.tradeprocessor.domain.PriceDto;
import com.tradeprocessor.domain.RiskAssessmentDto;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.domain.TradeDto;
import com.tradeprocessor.service.RiskAssessmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Delegates risk assessment to the Python FastAPI risk engine, which owns
 * all calculator logic (ZScore, VaR, MonteCarlo, DV01, BasisRisk, ML
 * anomaly detection) and asset-class-specific calculator selection.
 *
 * Takes the EnrichedTrade (not the raw Trade) so risk assessment uses
 * InstrumentEnricher's authoritative instrument data, not the placeholder
 * InstrumentDetails guessed from the raw request in toTradeDomain().
 */
@Slf4j
@AllArgsConstructor
@Component
public class DefaultRiskEngine implements RiskEngine {

    private final RiskAssessmentService riskAssessmentService;

    @Override
    public RiskAssessmentDto assessTrade(EnrichedTrade enrichedTrade) {
        TradeDto tradeDto = toTradeDto(enrichedTrade);
        RiskAssessmentDto evaluateTradeResp = riskAssessmentService.evaluateTrade(tradeDto);
        log.info("Response from Pythong code:" + evaluateTradeResp);
        return evaluateTradeResp;
    }

    private TradeDto toTradeDto(EnrichedTrade enrichedTrade) {
        Trade trade = enrichedTrade.trade();

        if (enrichedTrade.instrumentDetails() == null) {
            throw new IllegalStateException(
                "Trade " + trade.getTradeId() + " has no InstrumentDetails after enrichment — cannot determine asset class");
        }
        String assetClass = enrichedTrade.instrumentDetails().instrumentType().name();

        return new TradeDto(
                trade.getTradeId(),
                trade.getSymbol(),
                trade.getQuantity(),
                new PriceDto(trade.getPrice().amount(), trade.getPrice().currency()),
                assetClass
        );
    }
}