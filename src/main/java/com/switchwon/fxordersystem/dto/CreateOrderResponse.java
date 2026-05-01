package com.switchwon.fxordersystem.dto;

import com.switchwon.fxordersystem.entity.Orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateOrderResponse(
        BigDecimal fromAmount,
        String fromCurrency,
        BigDecimal toAmount,
        String toCurrency,
        BigDecimal tradeRate,
        LocalDateTime dateTime
) {
    public static CreateOrderResponse fromEntity(Orders entity) {
        return new CreateOrderResponse(
                entity.getFromAmount(),
                entity.getFromCurrency().name(),
                entity.getToAmount(),
                entity.getToCurrency().name(),
                entity.getTradeRate(),
                entity.getDateTime()
        );
    }
}
