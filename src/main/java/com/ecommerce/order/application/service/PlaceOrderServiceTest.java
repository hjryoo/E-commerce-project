package com.ecommerce.order.application.service;

import com.ecommerce.order.application.dto.PlaceOrderCommand;
import com.ecommerce.order.application.dto.OrderResult;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class PlaceOrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private EventPublisher eventPublisher;

    private PlaceOrderService placeOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        placeOrderService = new PlaceOrderService(
                orderRepository, userRepository, productRepository, eventPublisher);
    }

    @Test
    @DisplayName("정상적인 주문 생성이 성공한다")
    void placeOrder_ShouldSuccess_WhenValidRequest() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        PlaceOrderCommand command = new PlaceOrderCommand(
                userId,
                List.of(new PlaceOrderCommand.OrderItemRequest(productId, 2))
        );

        given(userRepository.existsById(userId)).willReturn(true);
        given(productRepository.existsById(productId)).willReturn(true);
        given(productRepository.getProductNames(List.of(productId)))
                .willReturn(Map.of(productId, "테스트 상품"));
        given(productRepository.getProductPrices(List.of(productId)))
                .willReturn(Map.of(productId, new BigDecimal("10000")));
        given(productRepository.getProductStock(productId)).willReturn(10);
        given(userRepository.getUserBalance(userId)).willReturn(new BigDecimal("50000"));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.assignId(1L);
            return order;
        });

        // when
        OrderResult result = placeOrderService.placeOrder(command);

        // then
        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("20000"));

        verify(userRepository).updateBalance(eq(userId), eq(new BigDecimal("30000")));
        verify(productRepository).decreaseStock(productId, 2);
        verify(eventPublisher).publishOrderCreated(any(Order.class));
        verify(eventPublisher).publishPaymentCompleted(any(Order.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 주문 시 예외가 발생한다")
    void placeOrder_ShouldThrowException_WhenUserNotExists() {
        // given
        Long userId = 999L;
        PlaceOrderCommand command = new PlaceOrderCommand(
                userId,
                List.of(new PlaceOrderCommand.OrderItemRequest(1L, 1))
        );

        given(userRepository.existsById(userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> placeOrderService.placeOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다: " + userId);
    }

    @Test
    @DisplayName("재고가 부족할 때 예외가 발생한다")
    void placeOrder_ShouldThrowException_WhenInsufficientStock() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        PlaceOrderCommand command = new PlaceOrderCommand(
                userId,
                List.of(new PlaceOrderCommand.OrderItemRequest(productId, 10))
        );

        given(userRepository.existsById(userId)).willReturn(true);
        given(productRepository.existsById(productId)).willReturn(true);
        given(productRepository.getProductStock(productId)).willReturn(5);

        // when & then
        assertThatThrownBy(() -> placeOrderService.placeOrder(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("잔액이 부족할 때 예외가 발생한다")
    void placeOrder_ShouldThrowException_WhenInsufficientBalance() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        PlaceOrderCommand command = new PlaceOrderCommand(
                userId,
                List.of(new PlaceOrderCommand.OrderItemRequest(productId, 2))
        );

        given(userRepository.existsById(userId)).willReturn(true);
        given(productRepository.existsById(productId)).willReturn(true);
        given(productRepository.getProductNames(List.of(productId)))
                .willReturn(Map.of(productId, "테스트 상품"));
        given(productRepository.getProductPrices(List.of(productId)))
                .willReturn(Map.of(productId, new BigDecimal("10000")));
        given(productRepository.getProductStock(productId)).willReturn(10);
        given(userRepository.getUserBalance(userId)).willReturn(new BigDecimal("5000"));

        // when & then
        assertThatThrownBy(() -> placeOrderService.placeOrder(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("잔액이 부족합니다");
    }
}
