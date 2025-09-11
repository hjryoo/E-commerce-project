package com.ecommerce.order.application.dto;
import java.util.List;

public record PlaceOrderCommand(
        Long userId,
        List<OrderItemRequest> orderItems
) {
    public record OrderItemRequest(
            Long productId,
            int quantity
    ) {}
}