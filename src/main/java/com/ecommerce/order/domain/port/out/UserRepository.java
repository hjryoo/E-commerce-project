package com.ecommerce.order.domain.port.out;

import java.math.BigDecimal;

public interface UserRepository {
    boolean existsById(Long userId);
    BigDecimal getUserBalance(Long userId);
    void updateBalance(Long userId, BigDecimal newBalance);
}
