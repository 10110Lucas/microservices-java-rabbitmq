package com.algaworks.algasensors.temperature.monitoring.api.model;

import java.util.UUID;

public record PostSummaryOutput (
    UUID id,
    String title,
    String summary,
    String author
) {}
