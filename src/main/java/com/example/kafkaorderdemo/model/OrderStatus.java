package com.example.kafkaorderdemo.model;

public record OrderStatus(
        String orderId,
        String item,
        int quantity,
        Status status
) {

    public enum Status {
        RECEIVED,
        PROCESSING,
        COMPLETED
    }
}
