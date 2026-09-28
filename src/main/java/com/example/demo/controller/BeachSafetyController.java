package com.example.demo.controller;

import com.example.demo.dto.BeachSectorResponse;
import com.example.demo.dto.CreateBeachSectorRequest;
import com.example.demo.dto.CreateWaterSampleRequest;
import com.example.demo.dto.WaterSampleResponse;
import com.example.demo.service.BeachSafetyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beach")
public class BeachSafetyController {
    private final BeachSafetyService service;

    public BeachSafetyController(BeachSafetyService service) {
        this.service = service;
    }

    @PostMapping("/sectors")
    @ResponseStatus(HttpStatus.CREATED)
    public BeachSectorResponse createSector(@Valid @RequestBody CreateBeachSectorRequest request) {
        return service.createSector(request);
    }

    @GetMapping("/sectors")
    public List<BeachSectorResponse> getSectors() {
        return service.getSectors();
    }

    @PostMapping("/samples")
    @ResponseStatus(HttpStatus.CREATED)
    public WaterSampleResponse createSample(@Valid @RequestBody CreateWaterSampleRequest request) {
        return service.createSample(request);
    }

    @GetMapping("/sectors/{sectorId}/samples")
    public List<WaterSampleResponse> getSamples(@PathVariable Long sectorId) {
        return service.getSamples(sectorId);
    }

    @GetMapping("/samples")
    public List<WaterSampleResponse> getAllSamples() {
        return service.getAllSamples();
    }

    @PostMapping("/seed-demo")
    @ResponseStatus(HttpStatus.CREATED)
    public void seedDemoData() {
        service.seedDemoData();
    }
}
