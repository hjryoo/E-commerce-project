package com.ecommerce.point.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.ecommerce.point.service.dto.ChargePointCommand;

import java.math.BigDecimal;

public record ChargePointRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @NotNull(message = "충전 금액은 필수입니다")
        @Positive(message = "충전 금액은 0보다 커야 합니다")
        BigDecimal amount,

        String description
) {
    public ChargePointCommand toCommand() {
        return new ChargePointCommand(userId, amount, description);
    }
}
