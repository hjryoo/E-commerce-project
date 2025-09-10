package com.ecommerce.order.domain.port.in;

import com.ecommerce.order.application.dto.PlaceOrderCommand;
import com.ecommerce.order.application.dto.OrderResult;

public interface PlaceOrderUseCase {
    OrderResult placeOrder(PlaceOrderCommand command);
}
