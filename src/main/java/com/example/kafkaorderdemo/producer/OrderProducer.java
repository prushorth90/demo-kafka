package com.example.kafkaorderdemo.producer;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    // A topic is a named stream where Kafka stores related messages for consumers to read.
    public static final String ORDERS_CREATED_TOPIC = "orders.created";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    // KafkaTemplate is Spring's helper for publishing messages to Kafka.
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrder(OrderCreatedEvent event) {
        LOGGER.info("Publishing order {} to Kafka", event.orderId());

        // send() starts an asynchronous publish; the configured serializer converts the event to JSON.
        kafkaTemplate.send(ORDERS_CREATED_TOPIC, event);
    }
}
