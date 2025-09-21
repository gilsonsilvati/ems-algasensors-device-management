package com.algaworks.algasensors.device.management.api.model;

import jakarta.validation.constraints.NotBlank;

public record SensorInput(
        @NotBlank String name,
        @NotBlank String ip,
        @NotBlank String location,
        @NotBlank String protocol,
        @NotBlank String model) {
}
