package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.Order;

public interface EventPublisher {
    void publishOrderCreated(Order order);
    void publishPaymentCompleted(Order order);
}
