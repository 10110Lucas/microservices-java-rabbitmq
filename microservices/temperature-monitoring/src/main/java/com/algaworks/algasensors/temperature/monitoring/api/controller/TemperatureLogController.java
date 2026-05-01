package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensors/{sensorId}/temperatures")
@RequiredArgsConstructor
public class TemperatureLogController {

    private final TemperatureLogRepository repository;

    @GetMapping
    public Page<TemperatureLogData> search(@PathVariable String sensorId, Pageable pageable) {
        Page<TemperatureLog> tLogs = repository.findAllBySensorId(new SensorId(sensorId), pageable);

        return tLogs.map(tLog ->
                TemperatureLogData.builder()
                        .id(tLog.getId().getValue())
                        .value(tLog.getValue())
                        .registeredAt(tLog.getRegisteredAt())
                        .sensorId(tLog.getSensorId().getValue())
                        .build());
    }
}
