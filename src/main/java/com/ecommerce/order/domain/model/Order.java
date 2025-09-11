package com.ecommerce.order.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {
    private Long id;
    private final Long userId;
    private final List<OrderItem> orderItems;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private final LocalDateTime orderedAt;
    private LocalDateTime paidAt;

    // 생성자는 private - 정적 팩토리 메서드 사용
    private Order(Long userId, List<OrderItem> orderItems) {
        this.userId = userId;
        this.orderItems = new ArrayList<>(orderItems);
        this.status = OrderStatus.PENDING;
        this.totalAmount = calculateTotalAmount();
        this.orderedAt = LocalDateTime.now();
    }

    // 정적 팩토리 메서드
    public static Order create(Long userId, List<OrderItem> orderItems) {
        validateOrderCreation(userId, orderItems);
        return new Order(userId, orderItems);
    }

    // 비즈니스 로직: 총 금액 계산
    private BigDecimal calculateTotalAmount() {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 비즈니스 로직: 결제 완료 처리
    public void completePayment() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태가 아닌 주문은 결제할 수 없습니다.");
        }
        this.status = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    // 비즈니스 로직: 주문 취소
    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("결제 완료된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // 검증 로직
    private static void validateOrderCreation(Long userId, List<OrderItem> orderItems) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("주문 상품은 최소 1개 이상이어야 합니다.");
        }
    }

    // ID 할당 (패키지 프라이빗 - 영속성 어댑터만 접근 가능)
    void assignId(Long id) {
        this.id = id;
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public List<OrderItem> getOrderItems() { return new ArrayList<>(orderItems); }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderedAt() { return orderedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
}