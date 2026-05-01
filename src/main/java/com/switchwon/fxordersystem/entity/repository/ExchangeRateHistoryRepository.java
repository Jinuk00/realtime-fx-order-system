package com.switchwon.fxordersystem.entity.repository;

import com.switchwon.fxordersystem.enums.CurrencyCode;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistory, Long>, ExchangeRateHistoryRepositoryCustom {
    Optional<ExchangeRateHistory> findTopByCurrencyOrderByDateTimeDesc(CurrencyCode currency);
}
