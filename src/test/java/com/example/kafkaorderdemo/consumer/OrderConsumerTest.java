package com.example.kafkaorderdemo.consumer;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.producer.OrderProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class OrderConsumerTest {

    @Test
    void listensToOrdersCreatedAsOrderProcessingGroup() throws NoSuchMethodException {
        Method listenerMethod = OrderConsumer.class.getMethod("processOrder", OrderCreatedEvent.class);
        KafkaListener listener = listenerMethod.getAnnotation(KafkaListener.class);

        assertThat(listener.topics()).containsExactly(OrderProducer.ORDERS_CREATED_TOPIC);
        assertThat(listener.groupId()).isEqualTo("order-processing-group");
    }

    @Test
    void processesOrderAfterShortDelay(CapturedOutput output) {
        OrderConsumer consumer = new OrderConsumer();
        OrderCreatedEvent event = new OrderCreatedEvent("abc-123", "Burger", 2, 1_750_000_000_000L);
        Instant startedAt = Instant.now();

        consumer.processOrder(event);

        assertThat(Duration.between(startedAt, Instant.now())).isGreaterThanOrEqualTo(Duration.ofMillis(900));
        assertThat(output)
                .contains("Received order abc-123 from Kafka")
                .contains("Processing item Burger for order abc-123")
                .contains("Completed order abc-123");
    }
}
