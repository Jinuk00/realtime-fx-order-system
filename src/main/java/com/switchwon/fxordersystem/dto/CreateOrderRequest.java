package com.switchwon.fxordersystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @DecimalMin(value = "0", inclusive = false, message = "forexAmount는 0보다 커야 합니다.") BigDecimal forexAmount,
        @NotBlank String fromCurrency,
        @NotBlank String toCurrency
) {
}
