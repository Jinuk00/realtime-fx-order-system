package com.switchwon.fxordersystem.entity.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import com.switchwon.fxordersystem.enums.CurrencyCode;
import com.switchwon.fxordersystem.entity.ExchangeRateHistory;
import com.switchwon.fxordersystem.entity.QExchangeRateHistory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.switchwon.fxordersystem.entity.QExchangeRateHistory.*;

@RequiredArgsConstructor
public class ExchangeRateHistoryRepositoryImpl implements ExchangeRateHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ExchangeRateHistory> findLatestByCurrencies(List<CurrencyCode> currencies) {
        QExchangeRateHistory subHistory = new QExchangeRateHistory("subHistory");

        return queryFactory
                .selectFrom(exchangeRateHistory)
                .where(
                        exchangeRateHistory.currency.in(currencies),
                        exchangeRateHistory.dateTime.eq(
                                JPAExpressions
                                        .select(subHistory.dateTime.max())
                                        .from(subHistory)
                                        .where(subHistory.currency.eq(exchangeRateHistory.currency))
                        )
                )
                .fetch();
    }
}
