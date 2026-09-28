package com.example.demo.service;

import com.example.demo.dto.BeachSectorResponse;
import com.example.demo.dto.CreateBeachSectorRequest;
import com.example.demo.dto.CreateWaterSampleRequest;
import com.example.demo.dto.WaterSampleResponse;
import com.example.demo.model.BeachSector;
import com.example.demo.model.RiskLevel;
import com.example.demo.model.WaterSample;
import com.example.demo.repository.BeachSectorRepository;
import com.example.demo.repository.WaterSampleRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BeachSafetyService {
    private final BeachSectorRepository sectorRepository;
    private final WaterSampleRepository sampleRepository;

    public BeachSafetyService(BeachSectorRepository sectorRepository,
                              WaterSampleRepository sampleRepository) {
        this.sectorRepository = sectorRepository;
        this.sampleRepository = sampleRepository;
    }

    @Transactional
    public BeachSectorResponse createSector(CreateBeachSectorRequest request) {
        if (sectorRepository.existsByNameIgnoreCase(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A beach sector with this name already exists");
        }
        return toSectorResponse(sectorRepository.save(
                new BeachSector(request.name(), request.location())));
    }

    @Transactional(readOnly = true)
    public List<BeachSectorResponse> getSectors() {
        return sectorRepository.findAll().stream().map(this::toSectorResponse).toList();
    }

    @Transactional
    public WaterSampleResponse createSample(CreateWaterSampleRequest request) {
        BeachSector sector = sectorRepository.findById(request.sectorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Beach sector not found"));
        RiskLevel risk = request.bacteriaCount() > request.safetyThreshold()
                ? RiskLevel.RED : RiskLevel.GREEN;
        WaterSample sample = sampleRepository.save(new WaterSample(sector,
                request.collectedAt(), request.bacteriaCount(),
                request.safetyThreshold(), risk));
        return toSampleResponse(sample);
    }

    @Transactional(readOnly = true)
    public List<WaterSampleResponse> getSamples(Long sectorId) {
        if (!sectorRepository.existsById(sectorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Beach sector not found");
        }
        return sampleRepository.findBySectorIdOrderByCollectedAtDesc(sectorId)
                .stream().map(this::toSampleResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<WaterSampleResponse> getAllSamples() {
        return sampleRepository.findAllByOrderByCollectedAtDesc()
                .stream().map(this::toSampleResponse).toList();
    }

    @Transactional
    public void seedDemoData() {
        if (sectorRepository.count() > 0) {
            return;
        }
        BeachSector s1 = sectorRepository.save(new BeachSector("Central Public Beach Zone 1", "North Shore Coast, Towers 1-4"));
        BeachSector s2 = sectorRepository.save(new BeachSector("Emerald Bay South Shore", "Protected Coastal Marine Cove"));
        BeachSector s3 = sectorRepository.save(new BeachSector("Lighthouse Reef Sector 3", "Outer Headland & Surf Beach"));
        BeachSector s4 = sectorRepository.save(new BeachSector("Pelican Point Estuary", "River Inlet & Tidal Lagoon"));

        Instant now = Instant.now();
        sampleRepository.save(new WaterSample(s1, now.minusSeconds(7200), 38.0, 100.0, RiskLevel.GREEN));
        sampleRepository.save(new WaterSample(s2, now.minusSeconds(5400), 22.5, 100.0, RiskLevel.GREEN));
        sampleRepository.save(new WaterSample(s3, now.minusSeconds(1800), 164.0, 100.0, RiskLevel.RED));
        sampleRepository.save(new WaterSample(s4, now.minusSeconds(3600), 55.0, 100.0, RiskLevel.GREEN));
    }

    private BeachSectorResponse toSectorResponse(BeachSector sector) {
        return new BeachSectorResponse(sector.getId(), sector.getName(),
                sector.getLocation(), sector.isActive());
    }

    private WaterSampleResponse toSampleResponse(WaterSample sample) {
        return new WaterSampleResponse(sample.getId(), sample.getSector().getId(),
                sample.getSector().getName(), sample.getCollectedAt(),
                sample.getBacteriaCount(), sample.getSafetyThreshold(), sample.getRiskLevel());
    }
}
