package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.Order;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    void delete(Order order);
}