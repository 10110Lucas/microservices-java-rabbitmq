package com.algaworks.algasensors.temperature.monitoring.api.model;

import java.util.UUID;

public record PostProcessingRequest(
        UUID postId,
        String postBody
) {}
