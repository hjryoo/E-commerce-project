package com.ecommerce.point.controller.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        Long userId,
        BigDecimal balance
) {
    public static BalanceResponse of(Long userId, BigDecimal balance) {
        return new BalanceResponse(userId, balance);
    }
}
