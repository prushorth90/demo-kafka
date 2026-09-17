package com.example.kafkaorderdemo.controller;

import com.example.kafkaorderdemo.model.OrderCreatedEvent;
import com.example.kafkaorderdemo.producer.OrderProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderProducer orderProducer;

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

        verify(orderProducer).publishOrder(argThat(event -> hasOrderDetails(event, "Burger", 2)));
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
