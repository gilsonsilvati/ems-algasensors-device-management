package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorRepository sensorRepository;

    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable) {
        Page<Sensor> sensors = sensorRepository.findAll(pageable);

        return sensors.map(this::toModel);
    }

    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        Sensor sensor = getSensor(sensorId);

        return toModel(sensor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@Valid @RequestBody SensorInput input) {
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.name())
                .ip(input.ip())
                .location(input.location())
                .protocol(input.protocol())
                .model(input.model())
                .enabled(Boolean.FALSE)
                .build();

        sensor = sensorRepository.saveAndFlush(sensor);

        return toModel(sensor);
    }

    @PutMapping("{sensorId}")
    public SensorOutput update(@PathVariable TSID sensorId, @Valid @RequestBody SensorInput input) {
        Sensor sensor = getSensor(sensorId);
        sensor.setName(input.name());
        sensor.setIp(input.ip());
        sensor.setLocation(input.location());
        sensor.setProtocol(input.protocol());
        sensor.setModel(input.model());

        sensorRepository.saveAndFlush(sensor);

        return toModel(sensor);
    }

    @DeleteMapping("{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        Sensor sensor = getSensor(sensorId);

        sensorRepository.delete(sensor);
    }

    @PutMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enabled(@PathVariable TSID sensorId) {
        Sensor sensor = getSensor(sensorId);

        if (Boolean.FALSE.equals(sensor.getEnabled())) {
            sensor.setEnabled(Boolean.TRUE);
            sensorRepository.save(sensor);
        }
    }

    @DeleteMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disabled(@PathVariable TSID sensorId) {
        Sensor sensor = getSensor(sensorId);

        if (Boolean.TRUE.equals(sensor.getEnabled())) {
            sensor.setEnabled(Boolean.FALSE);
            sensorRepository.save(sensor);
        }
    }

    private SensorOutput toModel(Sensor sensor) {
        return new SensorOutput(
                sensor.getId().getValue(),
                sensor.getName(),
                sensor.getIp(),
                sensor.getLocation(),
                sensor.getProtocol(),
                sensor.getModel(),
                sensor.getEnabled()
        );
    }

    private Sensor getSensor(TSID sensorId) {
        return sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
