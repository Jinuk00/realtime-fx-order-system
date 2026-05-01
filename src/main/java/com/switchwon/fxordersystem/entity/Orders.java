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
@Table(name = "ORDERS")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fromAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode fromCurrency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal toAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode toCurrency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal tradeRate;

    @Column(nullable = false)
    private LocalDateTime dateTime;

}
