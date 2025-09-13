package com.ecommerce.point.service.dto;
import java.math.BigDecimal;

public record ChargePointCommand(
        Long userId,
        BigDecimal amount,
        String description
) {
    public ChargePointCommand {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
    }
}