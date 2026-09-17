package com.example.kafkaorderdemo.controller;

import com.example.kafkaorderdemo.model.EventLogEntry;
import com.example.kafkaorderdemo.service.EventLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventLogService eventLogService;

    public EventController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    @GetMapping
    public List<EventLogEntry> getEvents() {
        return eventLogService.findAllNewestFirst();
    }
}
