package com.example.kafkaorderdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotBlank String item,
        @Positive int quantity
) {
}
