package com.example.kafkaorderdemo.controller;

import com.example.kafkaorderdemo.model.CreateOrderRequest;
import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.producer.OrderProducer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
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

        orderProducer.publishOrder(event);
        return event;
    }
}
