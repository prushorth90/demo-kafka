package com.example.kafkaorderdemo.controller;

import com.example.kafkaorderdemo.config.WebConfig;
import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.model.OrderStatus;
import com.example.kafkaorderdemo.producer.OrderProducer;
import com.example.kafkaorderdemo.service.EventLogService;
import com.example.kafkaorderdemo.service.OrderStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(WebConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderProducer orderProducer;

    @MockitoBean
    private OrderStore orderStore;

    @MockitoBean
    private EventLogService eventLogService;

    @Test
    void createsAndPublishesOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"item":"Burger","quantity":2}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").isNotEmpty())
                .andExpect(jsonPath("$.item").value("Burger"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.createdAt").isNumber());

    verify(orderStore).save(argThat(event -> hasOrderDetails(event, "Burger", 2)));
        verify(orderProducer).publishOrder(argThat(event -> hasOrderDetails(event, "Burger", 2)));
    verify(eventLogService).record(argThat(message -> message.startsWith("REST API received order ")));
    }

    @Test
    void returnsAllOrders() throws Exception {
    when(orderStore.findAll()).thenReturn(List.of(
        new OrderStatus("abc-123", "Burger", 2, OrderStatus.Status.PROCESSING)
    ));

    mockMvc.perform(get("/api/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderId").value("abc-123"))
        .andExpect(jsonPath("$[0].status").value("PROCESSING"));
    }

    @Test
    void returnsOrderById() throws Exception {
    when(orderStore.findById("abc-123")).thenReturn(Optional.of(
        new OrderStatus("abc-123", "Burger", 2, OrderStatus.Status.COMPLETED)
    ));

    mockMvc.perform(get("/api/orders/abc-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.item").value("Burger"))
        .andExpect(jsonPath("$.quantity").value(2))
        .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void returnsNotFoundForUnknownOrder() throws Exception {
    when(orderStore.findById("missing")).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/orders/missing"))
        .andExpect(status().isNotFound());
    }

    @Test
    void allowsRequestsFromViteDevelopmentServer() throws Exception {
        mockMvc.perform(options("/api/orders")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    @Test
    void rejectsBlankItem() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"item":" ","quantity":2}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderProducer);
    }

    @Test
    void rejectsNonPositiveQuantity() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"item":"Burger","quantity":0}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderProducer);
    }

    private boolean hasOrderDetails(OrderCreatedEvent event, String item, int quantity) {
        return event.orderId() != null
                && !event.orderId().isBlank()
                && event.item().equals(item)
                && event.quantity() == quantity
                && event.createdAt() > 0;
    }
}
