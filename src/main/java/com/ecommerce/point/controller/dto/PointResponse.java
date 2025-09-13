package com.ecommerce.point.controller.dto;
import com.ecommerce.point.entity.UserBalance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointResponse(
        Long userId,
        BigDecimal balance,
        LocalDateTime updatedAt
) {
    public static PointResponse from(UserBalance userBalance) {
        return new PointResponse(
                userBalance.getUserId(),
                userBalance.getBalance(),
                userBalance.getUpdatedAt()
        );
    }
}