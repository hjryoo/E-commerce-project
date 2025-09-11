package com.ecommerce.order.adapter.out.persistence;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.port.out.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final SpringDataOrderRepository jpaRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = toEntity(order);
        OrderJpaEntity savedEntity = jpaRepository.save(entity);
        order.assignId(savedEntity.getId());
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public void delete(Order order) {
        jpaRepository.deleteById(order.getId());
    }

    private OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setUserId(order.getUserId());
        entity.setStatus(mapStatus(order.getStatus()));
        entity.setTotalAmount(order.getTotalAmount());
        entity.setOrderedAt(order.getOrderedAt());
        entity.setPaidAt(order.getPaidAt());

        List<OrderItemJpaEntity> itemEntities = order.getOrderItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList();
        entity.setOrderItems(itemEntities);

        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> orderItems = entity.getOrderItems().stream()
                .map(this::toItemDomain)
                .toList();

        Order order = Order.create(entity.getUserId(), orderItems);
        order.assignId(entity.getId());

        if (entity.getStatus() == OrderJpaEntity.OrderStatus.PAID) {
            order.completePayment();
        }

        return order;
    }

    private OrderItemJpaEntity toItemEntity(OrderItem item, OrderJpaEntity order) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setOrder(order);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setQuantity(item.getQuantity());
        return entity;
    }

    private OrderItem toItemDomain(OrderItemJpaEntity entity) {
        return OrderItem.create(
                entity.getProductId(),
                entity.getProductName(),
                entity.getUnitPrice(),
                entity.getQuantity()
        );
    }

    private OrderJpaEntity.OrderStatus mapStatus(com.ecommerce.order.domain.model.OrderStatus status) {
        return switch (status) {
            case PENDING -> OrderJpaEntity.OrderStatus.PENDING;
            case PAID -> OrderJpaEntity.OrderStatus.PAID;
            case CANCELLED -> OrderJpaEntity.OrderStatus.CANCELLED;
        };
    }
}