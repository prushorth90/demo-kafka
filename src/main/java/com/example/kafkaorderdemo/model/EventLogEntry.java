package com.example.kafkaorderdemo.model;

public record EventLogEntry(
        long id,
        long timestamp,
        String message
) {
}
