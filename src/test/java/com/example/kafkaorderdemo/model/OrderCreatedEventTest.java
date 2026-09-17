package com.example.kafkaorderdemo.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedEventTest {

    @Test
    void exposesOrderDetails() {
        OrderCreatedEvent event = new OrderCreatedEvent("order-123", "coffee", 2, 1_750_000_000_000L);

        assertThat(event.orderId()).isEqualTo("order-123");
        assertThat(event.item()).isEqualTo("coffee");
        assertThat(event.quantity()).isEqualTo(2);
        assertThat(event.createdAt()).isEqualTo(1_750_000_000_000L);
    }
}
