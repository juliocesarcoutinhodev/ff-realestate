package br.com.fabriciofaceroli.photo.domain.model;

import java.util.UUID;

public record PropertyPhoto(
        UUID id,
        UUID propertyId,
        String url,
        String fileName,
        boolean cover,
        int orderIndex
) {}
