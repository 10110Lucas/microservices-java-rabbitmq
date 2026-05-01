package com.algaworks.algasensors.temperature.monitoring.api.model;

import java.math.BigDecimal;
import java.util.UUID;

public record PostOutput (
    UUID id,
    String title,
    String body,
    String author,
    Integer wordCount,
    BigDecimal calculatedValue
) {}
