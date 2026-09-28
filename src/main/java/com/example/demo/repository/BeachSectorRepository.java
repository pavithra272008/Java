package com.example.demo.repository;

import com.example.demo.model.BeachSector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeachSectorRepository extends JpaRepository<BeachSector, Long> {
    boolean existsByNameIgnoreCase(String name);
}
