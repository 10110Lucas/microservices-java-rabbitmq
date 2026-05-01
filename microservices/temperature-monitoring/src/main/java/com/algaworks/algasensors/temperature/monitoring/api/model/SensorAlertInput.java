package com.algaworks.algasensors.temperature.monitoring.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorAlertInput {

    private Double maxTemperature;
    private Double minTemperature;
}
