package com.ecommerce.order.config;

import com.ecommerce.order.application.service.PlaceOrderService;
import com.ecommerce.order.domain.port.in.PlaceOrderUseCase;
import com.ecommerce.order.domain.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

    @Bean
    public PlaceOrderUseCase placeOrderUseCase(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            EventPublisher eventPublisher) {

        return new PlaceOrderService(
                orderRepository,
                userRepository,
                productRepository,
                eventPublisher
        );
    }
}
