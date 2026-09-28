package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateSalesOrderRequest(
    @NotNull Long customerId,
    Long sectorId,
    @NotNull @Positive BigDecimal totalAmount,
    String description
) {}
