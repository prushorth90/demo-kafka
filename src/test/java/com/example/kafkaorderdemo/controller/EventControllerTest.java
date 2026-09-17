package com.example.kafkaorderdemo.controller;

import com.example.kafkaorderdemo.config.WebConfig;
import com.example.kafkaorderdemo.model.EventLogEntry;
import com.example.kafkaorderdemo.service.EventLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(WebConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventLogService eventLogService;

    @Test
    void returnsEventsNewestFirst() throws Exception {
        when(eventLogService.findAllNewestFirst()).thenReturn(List.of(
                new EventLogEntry(2, 2_000, "newest event"),
                new EventLogEntry(1, 1_000, "oldest event")
        ));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("newest event"))
                .andExpect(jsonPath("$[1].message").value("oldest event"));
    }
}
