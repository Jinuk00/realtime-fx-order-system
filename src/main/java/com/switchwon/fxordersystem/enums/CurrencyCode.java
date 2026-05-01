package com.switchwon.fxordersystem.enums;

import com.switchwon.fxordersystem.common.BusinessException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum CurrencyCode {
    /** API·DB 정렬용. KRW는 외화 목록에 포함되지 않음 */
    KRW(0),
    USD(1),
    JPY(2),
    CNY(3),
    EUR(4);

    private final int sortOrder;

    CurrencyCode(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    /** 환율 API·수집 등에서 쓰는 외화 목록 (sortOrder 순) */
    public static List<CurrencyCode> tradableForexInSortOrder() {
        return Arrays.stream(values())
                .filter(CurrencyCode::isTradableForex)
                .sorted(Comparator.comparingInt(CurrencyCode::getSortOrder))
                .toList();
    }

    public static CurrencyCode from(String value) {
        try {
            return CurrencyCode.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("INVALID_CURRENCY", "지원하지 않는 통화 코드입니다: " + value);
        }
    }

    public boolean isTradableForex() {
        return this == USD || this == JPY || this == CNY || this == EUR;
    }
}
