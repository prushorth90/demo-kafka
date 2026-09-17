package com.example.kafkaorderdemo.model;

public record OrderCreatedEvent(
        String orderId,
        String item,
        int quantity,
        long createdAt
) {
}
