package com.example.financialapp.infrastructure.web.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountRequest(
        UUID customerId,
        String type
) {}
