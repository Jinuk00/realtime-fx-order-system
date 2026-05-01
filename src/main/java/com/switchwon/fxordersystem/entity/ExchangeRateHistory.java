package com.switchwon.fxordersystem.entity;

import com.switchwon.fxordersystem.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "EXCHANGE_RATE_HISTORY")
public class ExchangeRateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal buyRate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal tradeStanRate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal sellRate;

    @Column(nullable = false)
    private LocalDateTime dateTime;

}
