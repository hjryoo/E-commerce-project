package com.ecommerce.order.domain.model;

public enum OrderStatus {
    PENDING("결제 대기"),
    PAID("결제 완료"),
    CANCELLED("취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
