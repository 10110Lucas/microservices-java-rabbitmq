package com.algaworks.algasensors.temperature.monitoring.api.model;

import java.math.BigDecimal;
import java.util.UUID;

public record PostProcessingResult (
    UUID postId,
    Integer wordCount,
    BigDecimal calculatedValue
) {}
