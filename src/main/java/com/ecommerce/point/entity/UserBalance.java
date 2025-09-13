package com.ecommerce.point.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_balances")
public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version; // 낙관적 락을 위한 버전 필드

    // 기본 생성자 (JPA 요구사항)
    protected UserBalance() {}

    // 생성자
    public UserBalance(Long userId, BigDecimal initialBalance) {
        this.userId = userId;
        this.balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 정적 팩토리 메서드
    public static UserBalance create(Long userId) {
        return new UserBalance(userId, BigDecimal.ZERO);
    }

    public static UserBalance create(Long userId, BigDecimal initialBalance) {
        return new UserBalance(userId, initialBalance);
    }

    // 비즈니스 로직 - 포인트 충전
    public void charge(BigDecimal amount) {
        validateChargeAmount(amount);
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 - 포인트 사용
    public void use(BigDecimal amount) {
        validateUseAmount(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("잔액이 부족합니다. 현재 잔액: " + this.balance + ", 사용 요청 금액: " + amount);
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    // 검증 로직
    private void validateChargeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("충전 금액은 0보다 커야 합니다.");
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new InvalidAmountException("한번에 충전할 수 있는 최대 금액은 1,000,000원입니다.");
        }
    }

    private void validateUseAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("사용 금액은 0보다 커야 합니다.");
        }
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
