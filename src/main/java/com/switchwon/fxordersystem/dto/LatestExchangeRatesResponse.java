package com.switchwon.fxordersystem.dto;

import java.util.List;

public record LatestExchangeRatesResponse(List<ExchangeRateItem> exchangeRateList) {
}
