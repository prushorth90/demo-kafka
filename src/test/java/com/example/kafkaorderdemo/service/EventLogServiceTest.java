package com.example.kafkaorderdemo.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventLogServiceTest {

    @Test
    void returnsNewestEventsFirst() {
        EventLogService eventLogService = new EventLogService();

        eventLogService.record("first event");
        eventLogService.record("second event");

        assertThat(eventLogService.findAllNewestFirst())
                .extracting(event -> event.message())
                .containsExactly("second event", "first event");
        assertThat(eventLogService.findAllNewestFirst())
                .extracting(event -> event.id())
                .doesNotHaveDuplicates();
    }
}
