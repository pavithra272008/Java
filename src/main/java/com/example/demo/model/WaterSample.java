package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "water_samples")
public class WaterSample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sector_id", nullable = false)
    private BeachSector sector;

    @Column(nullable = false)
    private Instant collectedAt;

    @Column(nullable = false)
    private double bacteriaCount;

    @Column(nullable = false)
    private double safetyThreshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    protected WaterSample() {}

    public WaterSample(BeachSector sector, Instant collectedAt, double bacteriaCount,
                       double safetyThreshold, RiskLevel riskLevel) {
        this.sector = sector;
        this.collectedAt = collectedAt;
        this.bacteriaCount = bacteriaCount;
        this.safetyThreshold = safetyThreshold;
        this.riskLevel = riskLevel;
    }

    public Long getId() { return id; }
    public BeachSector getSector() { return sector; }
    public Instant getCollectedAt() { return collectedAt; }
    public double getBacteriaCount() { return bacteriaCount; }
    public double getSafetyThreshold() { return safetyThreshold; }
    public RiskLevel getRiskLevel() { return riskLevel; }
}
