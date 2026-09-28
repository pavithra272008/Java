package com.example.demo.dto;

import com.example.demo.model.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateContactRequest(
    @NotBlank String name,
    @NotNull ContactType type,
    String email,
    String phone,
    String taxId
) {}
