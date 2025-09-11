package com.ecommerce.order.application.service;

import com.ecommerce.order.application.dto.PlaceOrderCommand;
import com.ecommerce.order.application.dto.OrderResult;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.port.in.PlaceOrderUseCase;
import com.ecommerce.order.domain.port.out.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public PlaceOrderService(OrderRepository orderRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository,
                             EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public OrderResult placeOrder(PlaceOrderCommand command) {
        // 1. 사용자 검증
        validateUser(command.userId());

        // 2. 상품 검증 및 정보 조회
        List<Long> productIds = command.orderItems().stream()
                .map(PlaceOrderCommand.OrderItemRequest::productId)
                .toList();

        validateProducts(productIds);
        Map<Long, String> productNames = productRepository.getProductNames(productIds);
        Map<Long, BigDecimal> productPrices = productRepository.getProductPrices(productIds);

        // 3. 재고 검증
        validateStock(command);

        // 4. 주문 아이템 생성
        List<OrderItem> orderItems = command.orderItems().stream()
                .map(item -> OrderItem.create(
                        item.productId(),
                        productNames.get(item.productId()),
                        productPrices.get(item.productId()),
                        item.quantity()
                ))
                .toList();

        // 5. 주문 생성
        Order order = Order.create(command.userId(), orderItems);

        // 6. 잔액 검증 및 차감
        processPayment(order);

        // 7. 재고 차감
        decreaseStock(command);

        // 8. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 9. 이벤트 발행
        eventPublisher.publishOrderCreated(savedOrder);
        eventPublisher.publishPaymentCompleted(savedOrder);

        return OrderResult.from(savedOrder);
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }
    }

    private void validateProducts(List<Long> productIds) {
        for (Long productId : productIds) {
            if (!productRepository.existsById(productId)) {
                throw new IllegalArgumentException("존재하지 않는 상품입니다: " + productId);
            }
        }
    }

    private void validateStock(PlaceOrderCommand command) {
        for (PlaceOrderCommand.OrderItemRequest item : command.orderItems()) {
            int availableStock = productRepository.getProductStock(item.productId());
            if (availableStock < item.quantity()) {
                throw new IllegalStateException(
                        String.format("재고가 부족합니다. 상품ID: %d, 요청수량: %d, 재고: %d",
                                item.productId(), item.quantity(), availableStock));
            }
        }
    }

    private void processPayment(Order order) {
        BigDecimal userBalance = userRepository.getUserBalance(order.getUserId());
        BigDecimal totalAmount = order.getTotalAmount();

        if (userBalance.compareTo(totalAmount) < 0) {
            throw new IllegalStateException("잔액이 부족합니다. 필요금액: " + totalAmount + ", 보유금액: " + userBalance);
        }

        // 잔액 차감
        BigDecimal newBalance = userBalance.subtract(totalAmount);
        userRepository.updateBalance(order.getUserId(), newBalance);

        // 결제 완료 처리
        order.completePayment();
    }

    private void decreaseStock(PlaceOrderCommand command) {
        for (PlaceOrderCommand.OrderItemRequest item : command.orderItems()) {
            productRepository.decreaseStock(item.productId(), item.quantity());
        }
    }
}