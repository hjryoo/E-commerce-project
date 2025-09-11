package com.ecommerce.order.domain.model;
import java.math.BigDecimal;

public class OrderItem {
    private final Long productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    private OrderItem(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public static OrderItem create(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        validateOrderItem(productId, productName, unitPrice, quantity);
        return new OrderItem(productId, productName, unitPrice, quantity);
    }

    // 비즈니스 로직: 총 가격 계산
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private static void validateOrderItem(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
    }

    // Getter 메서드들
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}