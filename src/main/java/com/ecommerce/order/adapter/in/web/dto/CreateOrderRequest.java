package com.ecommerce.order.adapter.in.web.dto;

import com.ecommerce.order.application.dto.PlaceOrderCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @Valid
        @NotNull(message = "주문 상품 목록은 필수입니다")
        List<OrderItemRequest> orderItems
) {
    public record OrderItemRequest(
            @NotNull(message = "상품 ID는 필수입니다")
            Long productId,

            @Positive(message = "수량은 1개 이상이어야 합니다")
            int quantity
    ) {}

    public PlaceOrderCommand toCommand() {
        List<PlaceOrderCommand.OrderItemRequest> commandItems = orderItems.stream()
                .map(item -> new PlaceOrderCommand.OrderItemRequest(
                        item.productId,
                        item.quantity
                ))
                .toList();

        return new PlaceOrderCommand(userId, commandItems);
    }
}

