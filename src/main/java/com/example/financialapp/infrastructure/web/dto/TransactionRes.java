package com.example.financialapp.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRes(
        UUID id,
        UUID account,
        String type,
        BigDecimal amount,
        Instant at,
        String note
) {
}
