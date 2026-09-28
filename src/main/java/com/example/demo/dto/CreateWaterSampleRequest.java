package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;

public record CreateWaterSampleRequest(
        @NotNull Long sectorId,
        @NotNull Instant collectedAt,
        @PositiveOrZero double bacteriaCount,
        @Positive double safetyThreshold) {}
