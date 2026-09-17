package com.example.kafkaorderdemo.producer;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.service.EventLogService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderProducerTest {

    @Test
    void publishesOrderToOrdersCreatedTopic() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        EventLogService eventLogService = mock(EventLogService.class);
        OrderProducer producer = new OrderProducer(kafkaTemplate, eventLogService);
        OrderCreatedEvent event = new OrderCreatedEvent("abc-123", "coffee", 2, 1_750_000_000_000L);

        producer.publishOrder(event);

        verify(kafkaTemplate).send(OrderProducer.ORDERS_CREATED_TOPIC, "abc-123", event);
        verify(eventLogService).record("Producer published abc-123 to orders.created");
    }
}
