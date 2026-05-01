package com.switchwon.fxordersystem.service;

import com.switchwon.fxordersystem.common.BusinessException;
import com.switchwon.fxordersystem.enums.CurrencyCode;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;
import com.switchwon.fxordersystem.entity.repository.ExchangeRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateHistoryRepository historyRepository;

    public List<ExchangeRateHistory> latestAll() {
        List<CurrencyCode> supportedForexCurrencies = CurrencyCode.tradableForexInSortOrder();
        List<ExchangeRateHistory> latestRates = historyRepository.findLatestByCurrencies(supportedForexCurrencies);
        Set<CurrencyCode> present = latestRates.stream()
                .map(ExchangeRateHistory::getCurrency)
                .collect(Collectors.toSet());
        for (CurrencyCode currency : supportedForexCurrencies) {
            if (!present.contains(currency)) {
                throw new BusinessException("RATE_NOT_FOUND", "최신 환율 데이터가 없습니다: " + currency);
            }
        }
        return latestRates.stream()
                .sorted(Comparator.comparingInt(h -> h.getCurrency().getSortOrder()))
                .toList();
    }

    public ExchangeRateHistory latestByCurrency(CurrencyCode currency) {
        if (!currency.isTradableForex()) {
            throw new BusinessException("INVALID_CURRENCY", "KRW는 환율 조회 대상이 아닙니다.");
        }
        return historyRepository.findTopByCurrencyOrderByDateTimeDesc(currency)
                .orElseThrow(() -> new BusinessException("RATE_NOT_FOUND", "최신 환율 데이터가 없습니다: " + currency));
    }
}
