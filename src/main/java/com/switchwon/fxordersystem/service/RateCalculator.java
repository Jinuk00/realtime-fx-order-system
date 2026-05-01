package com.switchwon.fxordersystem.service;

import com.switchwon.fxordersystem.enums.CurrencyCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RateCalculator {

    private static final BigDecimal BUY_SPREAD = new BigDecimal("1.05");
    private static final BigDecimal SELL_SPREAD = new BigDecimal("0.95");

    public BigDecimal roundRate(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal toTradeStandardRate(CurrencyCode currency, BigDecimal ratePerOneUnit) {
        BigDecimal standard = currency == CurrencyCode.JPY ? ratePerOneUnit.multiply(new BigDecimal("100")) : ratePerOneUnit;
        return roundRate(standard);
    }

    public BigDecimal buyRate(BigDecimal tradeStandardRate) {
        return roundRate(tradeStandardRate.multiply(BUY_SPREAD));
    }

    public BigDecimal sellRate(BigDecimal tradeStandardRate) {
        return roundRate(tradeStandardRate.multiply(SELL_SPREAD));
    }

    public BigDecimal floorKrw(BigDecimal krwAmount) {
        return krwAmount.setScale(0, RoundingMode.FLOOR);
    }
}
