package com.example.kafkaorderdemo.service;

import com.example.kafkaorderdemo.model.EventLogEntry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EventLogService {

    private final ConcurrentLinkedDeque<EventLogEntry> events = new ConcurrentLinkedDeque<>();
    private final AtomicLong nextId = new AtomicLong();

    public void record(String message) {
        events.addFirst(new EventLogEntry(
                nextId.incrementAndGet(),
                System.currentTimeMillis(),
                message
        ));
    }

    public List<EventLogEntry> findAllNewestFirst() {
        return List.copyOf(events);
    }
}
