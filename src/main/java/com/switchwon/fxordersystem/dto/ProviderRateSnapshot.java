package com.switchwon.fxordersystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProviderRateSnapshot(
        BigDecimal tradeStanRate,
        BigDecimal buyRate,
        BigDecimal sellRate,
        LocalDateTime dateTime
) {
}
