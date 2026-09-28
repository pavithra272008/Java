package com.example.demo.repository;

import com.example.demo.model.WaterSample;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterSampleRepository extends JpaRepository<WaterSample, Long> {
    List<WaterSample> findBySectorIdOrderByCollectedAtDesc(Long sectorId);
    List<WaterSample> findAllByOrderByCollectedAtDesc();
}
