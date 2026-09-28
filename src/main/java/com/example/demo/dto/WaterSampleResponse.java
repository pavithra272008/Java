package com.example.demo.dto;

import com.example.demo.model.RiskLevel;
import java.time.Instant;

public record WaterSampleResponse(Long id, Long sectorId, String sectorName,
                                  Instant collectedAt, double bacteriaCount,
                                  double safetyThreshold, RiskLevel riskLevel) {}
