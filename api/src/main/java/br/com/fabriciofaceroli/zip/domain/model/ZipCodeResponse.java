package br.com.fabriciofaceroli.zip.domain.model;

import java.io.Serializable;

public record ZipCodeResponse(
        String code,
        String street,
        String district,
        String city,
        String state
) implements Serializable {}
