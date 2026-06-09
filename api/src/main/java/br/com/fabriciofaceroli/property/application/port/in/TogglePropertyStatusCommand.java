package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;

import java.util.UUID;

public record TogglePropertyStatusCommand(UUID id, PropertyStatus status) {}
