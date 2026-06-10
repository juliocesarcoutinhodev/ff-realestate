package br.com.fabriciofaceroli.property.domain.model;

import java.util.UUID;

public record PhotoSummary(UUID id, String url, int order, boolean cover) {}
