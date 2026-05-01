package com.switchwon.fxordersystem.service;

import com.switchwon.fxordersystem.common.BusinessException;
import com.switchwon.fxordersystem.dto.CreateOrderRequest;
import com.switchwon.fxordersystem.enums.CurrencyCode;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;
import com.switchwon.fxordersystem.entity.Orders;
import com.switchwon.fxordersystem.entity.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class OrderService {

    private final ExchangeRateService exchangeRateService;
    private final RateCalculator rateCalculator;
    private final OrderRepository orderRepository;

    public Orders createOrder(CreateOrderRequest request) {
        CurrencyCode fromCurrency = CurrencyCode.from(request.fromCurrency());
        CurrencyCode toCurrency = CurrencyCode.from(request.toCurrency());
        validatePair(fromCurrency, toCurrency);

        BigDecimal forexAmount = request.forexAmount();
        LocalDateTime now = LocalDateTime.now();

        if (fromCurrency == CurrencyCode.KRW) {
            ExchangeRateHistory rate = exchangeRateService.latestByCurrency(toCurrency);
            BigDecimal krwAmount = rateCalculator.floorKrw(forexAmount.multiply(rate.getBuyRate()));
            return orderRepository.save(Orders.builder()
                    .fromAmount(krwAmount)
                    .fromCurrency(fromCurrency)
                    .toAmount(forexAmount)
                    .toCurrency(toCurrency)
                    .tradeRate(rate.getBuyRate())
                    .dateTime(now)
                    .build());
        }

        ExchangeRateHistory rate = exchangeRateService.latestByCurrency(fromCurrency);
        BigDecimal krwAmount = rateCalculator.floorKrw(forexAmount.multiply(rate.getSellRate()));
        return orderRepository.save(Orders.builder()
                .fromAmount(forexAmount)
                .fromCurrency(fromCurrency)
                .toAmount(krwAmount)
                .toCurrency(toCurrency)
                .tradeRate(rate.getSellRate())
                .dateTime(now)
                .build());
    }

    public List<Orders> listOrders() {
        return orderRepository.findAllByOrderByIdAsc();
    }

    private void validatePair(CurrencyCode fromCurrency, CurrencyCode toCurrency) {
        if (fromCurrency == toCurrency) {
            throw new BusinessException("INVALID_ORDER_PAIR", "from/to 통화가 동일합니다.");
        }
        if (!(fromCurrency == CurrencyCode.KRW || toCurrency == CurrencyCode.KRW)) {
            throw new BusinessException("INVALID_ORDER_PAIR", "KRW 기준 주문만 지원합니다.");
        }
        if (fromCurrency != CurrencyCode.KRW && !fromCurrency.isTradableForex()) {
            throw new BusinessException("INVALID_ORDER_PAIR", "지원하지 않는 외화입니다.");
        }
        if (toCurrency != CurrencyCode.KRW && !toCurrency.isTradableForex()) {
            throw new BusinessException("INVALID_ORDER_PAIR", "지원하지 않는 외화입니다.");
        }
    }

}
