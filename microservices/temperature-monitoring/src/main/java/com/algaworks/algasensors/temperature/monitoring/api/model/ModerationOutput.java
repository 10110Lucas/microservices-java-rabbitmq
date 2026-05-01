package com.algaworks.algasensors.temperature.monitoring.api.model;

public record ModerationOutput (
    Boolean approved,
    String reason
) {}
