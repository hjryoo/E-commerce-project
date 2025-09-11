package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResult(
        Long orderId,
        Long userId,
        List<OrderItemResult> orderItems,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime orderedAt
) {
    public static OrderResult from(Order order) {
        List<OrderItemResult> itemResults = order.getOrderItems().stream()
                .map(item -> new OrderItemResult(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .toList();

        return new OrderResult(
                order.getId(),
                order.getUserId(),
                itemResults,
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderedAt()
        );
    }

    public record OrderItemResult(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal totalPrice
    ) {}
}
