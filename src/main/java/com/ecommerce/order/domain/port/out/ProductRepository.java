package com.ecommerce.order.domain.port.out;

import java.math.BigDecimal;
import java.util.Map;

public interface ProductRepository {
    boolean existsById(Long productId);
    String getProductName(Long productId);
    BigDecimal getProductPrice(Long productId);
    int getProductStock(Long productId);
    void decreaseStock(Long productId, int quantity);
    Map<Long, String> getProductNames(java.util.List<Long> productIds);
    Map<Long, BigDecimal> getProductPrices(java.util.List<Long> productIds);
}

