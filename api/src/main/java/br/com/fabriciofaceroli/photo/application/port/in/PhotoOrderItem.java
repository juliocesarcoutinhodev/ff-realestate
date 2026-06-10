package br.com.fabriciofaceroli.photo.application.port.in;

import java.util.UUID;

public record PhotoOrderItem(UUID id, int orderIndex) {}
