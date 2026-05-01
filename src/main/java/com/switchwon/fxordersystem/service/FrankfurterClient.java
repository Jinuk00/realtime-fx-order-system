package com.switchwon.fxordersystem.service;

import com.switchwon.fxordersystem.common.BusinessException;
import com.switchwon.fxordersystem.client.EximRateFeignClient;
import com.switchwon.fxordersystem.dto.EximRateItem;
import com.switchwon.fxordersystem.dto.ProviderRateSnapshot;
import com.switchwon.fxordersystem.enums.CurrencyCode;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FrankfurterClient {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final EximRateFeignClient eximRateFeignClient;
    private final String authKey;
    private final RateCalculator rateCalculator;

    public FrankfurterClient(
            @Value("${fx.provider.auth-key:}") String authKeyFromEnv,
            EximRateFeignClient eximRateFeignClient,
            RateCalculator rateCalculator
    ) {
        this.authKey = resolveAuthKey(authKeyFromEnv);
        this.eximRateFeignClient = eximRateFeignClient;
        this.rateCalculator = rateCalculator;
    }

    public Map<CurrencyCode, ProviderRateSnapshot> fetchLatestRates() {
        if (authKey == null || authKey.isBlank()) {
            throw new BusinessException("PROVIDER_ERROR", "EXIM 인증키가 설정되지 않았습니다.");
        }

        String today = LocalDate.now().format(DATE_FORMATTER);
        List<EximRateItem> response = eximRateFeignClient.getLatestRates(authKey, today, "AP01");

        if (response == null || response.isEmpty()) {
            throw new BusinessException("PROVIDER_ERROR", "외부 환율 API 응답이 비어있습니다.");
        }

        LocalDateTime announcedAt = LocalDateTime.now();
        Map<CurrencyCode, ProviderRateSnapshot> result = new EnumMap<>(CurrencyCode.class);

        for (EximRateItem item : response) {
            if (!isSuccess(item.result())) {
                continue;
            }
            CurrencyCode currency = parseCurrency(item.curUnit());
            if (currency == CurrencyCode.KRW || !currency.isTradableForex()) {
                continue;
            }
            BigDecimal tradeRate = parseRate(item.dealBaseRate());
            BigDecimal rawBuyRate = parseRate(item.ttb());
            BigDecimal rawSellRate = parseRate(item.tts());
            int providerUnit = parseProviderUnit(item.curUnit());

            if (tradeRate == null) {
                continue;
            }

            BigDecimal normalizedTradeRate = normalizeToTradeStandardRate(currency, tradeRate, providerUnit);
            BigDecimal buyRate = rawBuyRate == null
                    ? rateCalculator.buyRate(normalizedTradeRate)
                    : normalizeToTradeStandardRate(currency, rawBuyRate, providerUnit);
            BigDecimal sellRate = rawSellRate == null
                    ? rateCalculator.sellRate(normalizedTradeRate)
                    : normalizeToTradeStandardRate(currency, rawSellRate, providerUnit);

            result.put(currency, new ProviderRateSnapshot(
                    normalizedTradeRate,
                    buyRate,
                    sellRate,
                    announcedAt
            ));
        }

        if (result.isEmpty()) {
            throw new BusinessException("PROVIDER_ERROR", "유효한 환율 데이터가 없습니다.");
        }
        return result;
    }

    private static CurrencyCode parseCurrency(String value) {
        if (value == null || value.isBlank()) {
            return CurrencyCode.KRW;
        }
        String normalized = value.toUpperCase()
                .replace("(100)", "")
                .trim();
        if ("CNH".equals(normalized)) {
            normalized = "CNY";
        }
        try {
            return CurrencyCode.from(normalized);
        } catch (BusinessException ex) {
            return CurrencyCode.KRW;
        }
    }

    private static BigDecimal parseRate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private BigDecimal normalizeToTradeStandardRate(CurrencyCode currency, BigDecimal rate, int providerUnit) {
        if (currency != CurrencyCode.JPY) {
            return rateCalculator.roundRate(rate);
        }

        BigDecimal ratePerOneUnit = providerUnit == 100
                ? rate.movePointLeft(2)
                : rate;
        return rateCalculator.toTradeStandardRate(currency, ratePerOneUnit);
    }

    private static int parseProviderUnit(String curUnit) {
        if (curUnit != null && curUnit.contains("(100)")) {
            return 100;
        }
        return 1;
    }

    private static boolean isSuccess(String result) {
        return "1".equals(result);
    }

    private static String resolveAuthKey(String authKeyFromEnv) {
        if (authKeyFromEnv != null && !authKeyFromEnv.isBlank()) {
            return authKeyFromEnv;
        }
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String authKey = dotenv.get("EXIM_AUTH_KEY");
            if (authKey != null && !authKey.isBlank()) {
                return authKey;
            }
        } catch (DotenvException ignored) {
            // .env 형식 오류가 있어도 시스템 환경변수 fallback 경로를 타도록 무시
        }
        return System.getenv("EXIM_AUTH_KEY");
    }

}
