package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorDetailOutput;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorRepository sensorRepository;
    private final SensorMonitoringClient client;

    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable) {
        Page<Sensor> sensors = sensorRepository.findAll(pageable);
        return sensors.map(this::convertToModel);
    }

    @GetMapping("/{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(sensor);
    }

    @GetMapping("/{sensorId}/detail")
    public SensorDetailOutput getOneWithDetail(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        SensorOutput sensorOutput = convertToModel(sensor);
        SensorMonitoringOutput monitoringOutput = client.getDetail(sensorId);

        return SensorDetailOutput.builder()
                .sensor(sensorOutput)
                .monitoring(monitoringOutput)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        Sensor sensor = Sensor.builder()
            .id(new SensorId(IdGenerator.generateTSID()))
            .name(input.getName())
            .ip(input.getIp())
            .location(input.getLocation())
            .protocol(input.getProtocol())
            .model(input.getModel())
            .enabled(Boolean.FALSE)
        .build();
        sensor = sensorRepository.saveAndFlush(sensor);
        return convertToModel(sensor);
    }

    @PutMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.OK)
    public SensorOutput update(@PathVariable TSID sensorId, @RequestBody SensorInput input) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setName(input.getName());
        sensor.setIp(input.getIp());
        sensor.setLocation(input.getLocation());
        sensor.setProtocol(input.getProtocol());
        sensor.setModel(input.getModel());
        sensor = sensorRepository.saveAndFlush(sensor);
        return convertToModel(sensor);
    }

    @PutMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEnable(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(true);
        sensorRepository.saveAndFlush(sensor);

        client.enableMonitoring(sensorId);
    }

    @DeleteMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDisable(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(false);
        sensorRepository.saveAndFlush(sensor);

        client.disableMonitoring(sensorId);
    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        sensorRepository.deleteById(new SensorId(sensorId));
    }

    private SensorOutput convertToModel(Sensor sensor) {
        return SensorOutput.builder()
            .id(sensor.getId().getValue())
            .name(sensor.getName())
            .ip(sensor.getIp())
            .location(sensor.getLocation())
            .protocol(sensor.getProtocol())
            .model(sensor.getModel())
            .enabled(sensor.getEnabled())
        .build();
    }
}
