package com.switchwon.fxordersystem.service;

import com.switchwon.fxordersystem.dto.ProviderRateSnapshot;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;
import com.switchwon.fxordersystem.entity.repository.ExchangeRateHistoryRepository;
import com.switchwon.fxordersystem.enums.CurrencyCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateScheduleService {

    private static final List<CurrencyCode> TARGETS = CurrencyCode.tradableForexInSortOrder();

    private final FrankfurterClient frankfurterClient;
    private final RateCalculator rateCalculator;
    private final ExchangeRateHistoryRepository historyRepository;

    @Scheduled(initialDelay = 3000, fixedDelayString = "${fx.scheduler.ms:60000}")
    public void collect() {
        LocalDateTime now = LocalDateTime.now();
        try {
            Map<CurrencyCode, ProviderRateSnapshot> rates = frankfurterClient.fetchLatestRates();
            for (CurrencyCode currency : TARGETS) {
                ProviderRateSnapshot snapshot = rates.get(currency);
                if (snapshot == null) {
                    throw new IllegalStateException("누락된 통화 환율 데이터: " + currency);
                }
                saveSnapshot(
                        currency,
                        rateCalculator.roundRate(snapshot.tradeStanRate()),
                        rateCalculator.roundRate(snapshot.buyRate()),
                        rateCalculator.roundRate(snapshot.sellRate()),
                        snapshot.dateTime() == null ? now : snapshot.dateTime()
                );
            }
        } catch (Exception ex) {
            for (CurrencyCode currency : TARGETS) {
                BigDecimal fallbackTrade = fallbackTradeStandard(currency);
                saveSnapshot(
                        currency,
                        fallbackTrade,
                        rateCalculator.buyRate(fallbackTrade),
                        rateCalculator.sellRate(fallbackTrade),
                        now
                );
            }
        }
    }

    private BigDecimal fallbackTradeStandard(CurrencyCode currency) {
        BigDecimal base = historyRepository.findTopByCurrencyOrderByDateTimeDesc(currency)
                .map(ExchangeRateHistory::getTradeStanRate)
                .orElseGet(() -> defaultRate(currency));
        BigDecimal randomDelta = new BigDecimal(ThreadLocalRandom.current().nextDouble(-0.005, 0.005));
        return rateCalculator.roundRate(base.multiply(BigDecimal.ONE.add(randomDelta)));
    }

    private BigDecimal defaultRate(CurrencyCode currency) {
        return switch (currency) {
            case USD -> new BigDecimal("1400.00");
            case JPY -> new BigDecimal("930.00");
            case CNY -> new BigDecimal("190.00");
            case EUR -> new BigDecimal("1520.00");
            default -> new BigDecimal("1000.00");
        };
    }

    private void saveSnapshot(
            CurrencyCode currency,
            BigDecimal tradeStandard,
            BigDecimal buyRate,
            BigDecimal sellRate,
            LocalDateTime dateTime
    ) {
        ExchangeRateHistory entity = ExchangeRateHistory.builder()
                .currency(currency)
                .buyRate(buyRate)
                .tradeStanRate(tradeStandard)
                .sellRate(sellRate)
                .dateTime(dateTime)
                .build();
        historyRepository.save(entity);
    }
}
