package com.example.kafkaorderdemo.controller;

import com.example.kafkaorderdemo.model.CreateOrderRequest;
import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.model.OrderStatus;
import com.example.kafkaorderdemo.producer.OrderProducer;
import com.example.kafkaorderdemo.service.EventLogService;
import com.example.kafkaorderdemo.service.OrderStore;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderStore orderStore;
    private final EventLogService eventLogService;

    public OrderController(OrderProducer orderProducer, OrderStore orderStore, EventLogService eventLogService) {
        this.orderProducer = orderProducer;
        this.orderStore = orderStore;
        this.eventLogService = eventLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedEvent createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                request.item(),
                request.quantity(),
                System.currentTimeMillis()
        );

            eventLogService.record("REST API received order " + event.orderId());
        orderStore.save(event);
        orderProducer.publishOrder(event);
        return event;
    }

    @GetMapping
    public List<OrderStatus> getOrders() {
        return orderStore.findAll();
    }

    @GetMapping("/{orderId}")
    public OrderStatus getOrder(@PathVariable String orderId) {
        return orderStore.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}
