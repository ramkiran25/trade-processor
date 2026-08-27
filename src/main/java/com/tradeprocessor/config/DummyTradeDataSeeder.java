package com.tradeprocessor.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.tradeprocessor.domain.InstrumentDetails;
import com.tradeprocessor.domain.InstrumentType;
import com.tradeprocessor.domain.Money;
import com.tradeprocessor.domain.Trade;
import com.tradeprocessor.domain.TradeStatus;
import com.tradeprocessor.mapper.TradeEntityMapper;
import com.tradeprocessor.repository.TradeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds a handful of dummy PROCESSED trades on startup so
 * TradeStatisticsService.calculateStatistics() has a real mean/stddev to work with instead of an
 * empty repository. Only runs once — skipped if the repository already has data, so it's harmless
 * against a persistent DB and a no-op every restart against H2 in-memory (which resets anyway).
 *
 * Notional values here (quantity=1, price=notional for readability) cluster around mean ~9916.67
 * with stddev ~1128.3. Use that to predict risk tiers for manual testing, e.g. curl with
 * quantity=1: price=10000 -> zScore ~0.07 -> LOW price=12737 -> zScore ~2.5 -> MEDIUM price=13866
 * -> zScore ~3.5 -> HIGH
 */
@Slf4j
@AllArgsConstructor
@Component
public class DummyTradeDataSeeder implements CommandLineRunner {

  private final TradeRepository tradeRepository;

  private static final BigDecimal[] SEED_NOTIONALS =
      {BigDecimal.valueOf(8000), BigDecimal.valueOf(8500), BigDecimal.valueOf(9000),
          BigDecimal.valueOf(9200), BigDecimal.valueOf(9500), BigDecimal.valueOf(9800),
          BigDecimal.valueOf(10000), BigDecimal.valueOf(10200), BigDecimal.valueOf(10500),
          BigDecimal.valueOf(10800), BigDecimal.valueOf(11500), BigDecimal.valueOf(12000)};

  @Override
  public void run(String... args) {
    if (tradeRepository.count() > 0) {
      log.info("Trades already present, skipping dummy data seeding");
      return;
    }

    InstrumentDetails equity = new InstrumentDetails(InstrumentType.EQUITY);
    LocalDateTime baseTime = LocalDateTime.now().minusDays(SEED_NOTIONALS.length);

    List<Trade> seedTrades = java.util.stream.IntStream.range(0, SEED_NOTIONALS.length)
        .mapToObj(i -> new Trade("seed-%03d".formatted(i + 1), BigDecimal.ONE,
            new Money(SEED_NOTIONALS[i], "EUR"), equity, baseTime.plusDays(i),
            TradeStatus.PROCESSED))
        .toList();

    seedTrades.forEach(trade -> tradeRepository.save(TradeEntityMapper.toEntity(trade)));

    log.info("Seeded {} dummy trades for risk/statistics testing", seedTrades.size());
  }
}
