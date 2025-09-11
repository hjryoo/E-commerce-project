package com.ecommerce.order.adapter.in.web;

import com.ecommerce.order.adapter.in.web.dto.CreateOrderRequest;
import com.ecommerce.order.adapter.in.web.dto.OrderResponse;
import com.ecommerce.order.application.dto.PlaceOrderCommand;
import com.ecommerce.order.application.dto.OrderResult;
import com.ecommerce.order.domain.port.in.PlaceOrderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        // DTO 변환
        PlaceOrderCommand command = request.toCommand();

        // 유스케이스 실행
        OrderResult result = placeOrderUseCase.placeOrder(command);

        // 응답 DTO 변환
        OrderResponse response = OrderResponse.from(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}