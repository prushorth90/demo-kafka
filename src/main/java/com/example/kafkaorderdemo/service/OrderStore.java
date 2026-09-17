package com.example.kafkaorderdemo.service;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderStore {

    private final ConcurrentHashMap<String, OrderStatus> orders = new ConcurrentHashMap<>();

    public OrderStatus save(OrderCreatedEvent event) {
        OrderStatus order = new OrderStatus(
                event.orderId(),
                event.item(),
                event.quantity(),
                OrderStatus.Status.RECEIVED
        );
        orders.put(order.orderId(), order);
        return order;
    }

    public void updateStatus(String orderId, OrderStatus.Status status) {
        orders.computeIfPresent(orderId, (id, order) -> new OrderStatus(
                order.orderId(),
                order.item(),
                order.quantity(),
                status
        ));
    }

    public List<OrderStatus> findAll() {
        return List.copyOf(orders.values());
    }

    public Optional<OrderStatus> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}
