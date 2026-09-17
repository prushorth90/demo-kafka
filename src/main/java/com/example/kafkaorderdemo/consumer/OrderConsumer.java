package com.example.kafkaorderdemo.consumer;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.model.OrderStatus;
import com.example.kafkaorderdemo.producer.OrderProducer;
import com.example.kafkaorderdemo.service.OrderStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    private final OrderStore orderStore;

    public OrderConsumer(OrderStore orderStore) {
        this.orderStore = orderStore;
    }

    // A consumer reads messages from Kafka; the producer does not call it directly because Kafka
    // connects them asynchronously and allows each side to run independently.
    // @KafkaListener tells Spring to invoke this method when a message arrives on the topic.
    // A consumer group shares a topic's messages among its members so each message is processed once per group.
    @KafkaListener(topics = OrderProducer.ORDERS_CREATED_TOPIC, groupId = "order-processing-group")
    public void processOrder(OrderCreatedEvent event) {
        LOGGER.info("Received order {} from Kafka", event.orderId());
        orderStore.updateStatus(event.orderId(), OrderStatus.Status.PROCESSING);
        LOGGER.info("Processing item {} for order {}", event.item(), event.orderId());

        try {
            Thread.sleep(1_000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
        }

        orderStore.updateStatus(event.orderId(), OrderStatus.Status.COMPLETED);
        LOGGER.info("Completed order {}", event.orderId());
    }
}
