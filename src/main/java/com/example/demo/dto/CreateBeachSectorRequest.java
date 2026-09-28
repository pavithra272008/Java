package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBeachSectorRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 250) String location) {}
