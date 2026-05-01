package com.switchwon.fxordersystem.entity.repository;

import com.switchwon.fxordersystem.enums.CurrencyCode;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;

import java.util.List;

public interface ExchangeRateHistoryRepositoryCustom {
    List<ExchangeRateHistory> findLatestByCurrencies(List<CurrencyCode> currencies);
}
