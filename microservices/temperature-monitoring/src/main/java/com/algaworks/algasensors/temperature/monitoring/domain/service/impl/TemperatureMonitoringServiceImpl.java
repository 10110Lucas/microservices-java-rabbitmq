package com.algaworks.algasensors.temperature.monitoring.domain.service.impl;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemperatureMonitoringServiceImpl implements TemperatureMonitoringService {

    private final SensorMonitoringRepository sensorMonitoringRepository;
    private final TemperatureLogRepository temperatureLogRepository;

    @Transactional
    public void processTemperatureReading(TemperatureLogData data) {
//        log.info("processTemperatureReading");
//        if (data.getValue().equals(10.5)) throw new RuntimeException("Testando Erro Forçado");

        sensorMonitoringRepository.findById(new SensorId(data.getSensorId()))
                .ifPresentOrElse(
                        sensor -> handleSensorMonitoring(data, sensor),
                        () -> logIgnoredTemperature(data)
                );
    }

    private void handleSensorMonitoring(TemperatureLogData data, SensorMonitoring sensor) {
        if (sensor != null && sensor.isEnabled()) {
            sensor.setLastTemperature(data.getValue());
            sensor.setUpdatedAt(OffsetDateTime.now());
            sensorMonitoringRepository.save(sensor);

            TemperatureLog tmpLog = TemperatureLog.builder()
                    .id(new TemperatureLogId(data.getId()))
                    .registeredAt(data.getRegisteredAt())
                    .value(data.getValue())
                    .sensorId(new SensorId(data.getSensorId()))
                    .build();
            temperatureLogRepository.save(tmpLog);
            log.info("Temperature Updated: SensorId {} Temp {}", data.getSensorId(), data.getValue());
        } else {
            logIgnoredTemperature(data);
        }
    }

    private void logIgnoredTemperature(TemperatureLogData data) {
        log.info("Temperature Ignored: SensorId {} Temp {}", data.getSensorId(), data.getValue());
    }
}
