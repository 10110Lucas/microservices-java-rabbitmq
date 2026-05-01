package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/sensors/{sensorId}/alert")
@RequiredArgsConstructor
public class SensorAlertController {

    private final SensorAlertRepository repository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SensorAlertOutput getSensorAlert(@PathVariable TSID sensorId){
        SensorAlert found = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return getSensorAlertOutput(found);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public SensorAlertOutput update(@PathVariable TSID sensorId, @RequestBody SensorAlert sensorAlert) {
        SensorAlert found = repository
                .findById(new SensorId(sensorId))
                .orElse(new SensorAlert(new SensorId(sensorId)));
        found.setMaxTemperature(sensorAlert.getMaxTemperature());
        found.setMinTemperature(sensorAlert.getMinTemperature());
        return getSensorAlertOutput(repository.saveAndFlush(found));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        SensorAlert found = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.deleteById(found.getId());
    }

    private static SensorAlertOutput getSensorAlertOutput(SensorAlert found) {
        return SensorAlertOutput.builder()
                .id(found.getId().getValue())
                .maxTemperature(found.getMaxTemperature())
                .minTemperature(found.getMinTemperature())
                .build();
    }
}
