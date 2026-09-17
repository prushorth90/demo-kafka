package com.example.kafkaorderdemo.producer;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderProducerTest {

    @Test
    void publishesOrderToOrdersCreatedTopic() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        OrderProducer producer = new OrderProducer(kafkaTemplate);
        OrderCreatedEvent event = new OrderCreatedEvent("abc-123", "coffee", 2, 1_750_000_000_000L);

        producer.publishOrder(event);

        verify(kafkaTemplate).send(OrderProducer.ORDERS_CREATED_TOPIC, event);
    }
}
