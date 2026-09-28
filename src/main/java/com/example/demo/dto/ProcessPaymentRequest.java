package com.example.demo.dto;

import com.example.demo.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProcessPaymentRequest(
    @NotNull @Positive BigDecimal amount,
    PaymentMethod paymentMethod
) {}
