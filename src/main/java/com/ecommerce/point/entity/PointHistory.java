package com.ecommerce.point.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 기본 생성자
    protected PointHistory() {}

    // 생성자
    private PointHistory(Long userId, PointTransactionType type, BigDecimal amount,
                         BigDecimal balanceAfter, String description) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    // 정적 팩토리 메서드들
    public static PointHistory createChargeHistory(Long userId, BigDecimal amount,
                                                   BigDecimal balanceAfter, String description) {
        return new PointHistory(userId, PointTransactionType.CHARGE, amount, balanceAfter, description);
    }

    public static PointHistory createUseHistory(Long userId, BigDecimal amount,
                                                BigDecimal balanceAfter, String description) {
        return new PointHistory(userId, PointTransactionType.USE, amount, balanceAfter, description);
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public PointTransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

// 포인트 거래 타입 열거형
enum PointTransactionType {
    CHARGE("충전"),
    USE("사용");

    private final String description;

    PointTransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
