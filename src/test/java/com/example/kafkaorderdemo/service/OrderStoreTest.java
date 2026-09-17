package com.example.kafkaorderdemo.service;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.model.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStoreTest {

    private final OrderStore orderStore = new OrderStore();

    @Test
    void savesAndUpdatesOrderStatus() {
        OrderCreatedEvent event = new OrderCreatedEvent("abc-123", "Burger", 2, 1_750_000_000_000L);

        OrderStatus received = orderStore.save(event);
        orderStore.updateStatus(event.orderId(), OrderStatus.Status.PROCESSING);

        assertThat(received.status()).isEqualTo(OrderStatus.Status.RECEIVED);
        assertThat(orderStore.findById(event.orderId()))
                .get()
                .extracting(OrderStatus::status)
                .isEqualTo(OrderStatus.Status.PROCESSING);
        assertThat(orderStore.findAll()).hasSize(1);
    }

    @Test
    void ignoresStatusUpdateForUnknownOrder() {
        orderStore.updateStatus("missing", OrderStatus.Status.COMPLETED);

        assertThat(orderStore.findById("missing")).isEmpty();
    }
}
