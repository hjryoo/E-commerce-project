package com.ecommerce.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 순수 도메인 객체 - JPA 어노테이션 없음
 * 비즈니스 로직과 상태를 담당
 */
public class UserBalance {

    private Long id;
    private final Long userId;
    private BigDecimal balance;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version; // 낙관적 락을 위한 버전 필드

    // 생성자
    private UserBalance(Long userId, BigDecimal balance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    // 정적 팩토리 메서드
    public static UserBalance create(Long userId) {
        return new UserBalance(userId, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
    }

    public static UserBalance create(Long userId, BigDecimal initialBalance) {
        return new UserBalance(userId, initialBalance, LocalDateTime.now(), LocalDateTime.now());
    }

    // 재구성을 위한 팩토리 메서드 (Infrastructure에서 사용)
    public static UserBalance reconstitute(Long id, Long userId, BigDecimal balance,
                                           LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        UserBalance userBalance = new UserBalance(userId, balance, createdAt, updatedAt);
        userBalance.id = id;
        userBalance.version = version;
        return userBalance;
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
            throw new IllegalStateException("잔액이 부족합니다. 현재 잔액: " + this.balance + ", 사용 요청 금액: " + amount);
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    // 검증 로직
    private void validateChargeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new IllegalArgumentException("한번에 충전할 수 있는 최대 금액은 1,000,000원입니다.");
        }
    }

    private void validateUseAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
    }

    // ID 할당 (Infrastructure 전용 - package-private)
    void assignId(Long id) {
        this.id = id;
    }

    void assignVersion(Long version) {
        this.version = version;
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}