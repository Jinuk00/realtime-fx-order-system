package com.switchwon.fxordersystem.controller;

import com.switchwon.fxordersystem.common.ApiResponse;
import com.switchwon.fxordersystem.dto.ExchangeRateItem;
import com.switchwon.fxordersystem.dto.LatestExchangeRatesResponse;
import com.switchwon.fxordersystem.enums.CurrencyCode;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;
import com.switchwon.fxordersystem.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/latest")
    public ApiResponse<LatestExchangeRatesResponse> latestAll() {
        List<ExchangeRateItem> items = exchangeRateService.latestAll().stream()
                .map(ExchangeRateItem::fromEntity)
                .toList();
        return ApiResponse.ok(new LatestExchangeRatesResponse(items));
    }

    @GetMapping("/latest/{currency}")
    public ApiResponse<ExchangeRateItem> latestOne(@PathVariable String currency) {
        ExchangeRateHistory history = exchangeRateService.latestByCurrency(CurrencyCode.from(currency));
        return ApiResponse.ok(ExchangeRateItem.fromEntity(history));
    }
}
