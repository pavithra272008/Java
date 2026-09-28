package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreatePurchaseOrderRequest(
    @NotNull Long vendorId,
    @NotNull @Positive BigDecimal totalAmount,
    String description
) {}
