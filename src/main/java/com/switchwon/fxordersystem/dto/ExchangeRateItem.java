package com.switchwon.fxordersystem.dto;

import com.switchwon.fxordersystem.entity.ExchangeRateHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateItem(
        String currency,
        BigDecimal buyRate,
        BigDecimal tradeStanRate,
        BigDecimal sellRate,
        LocalDateTime dateTime
) {
    public static ExchangeRateItem fromEntity(ExchangeRateHistory history) {
        return new ExchangeRateItem(
                history.getCurrency().name(),
                history.getBuyRate(),
                history.getTradeStanRate(),
                history.getSellRate(),
                history.getDateTime()
        );
    }
}
