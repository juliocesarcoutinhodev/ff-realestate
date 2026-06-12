package br.com.fabriciofaceroli.sitesettings.application.port.in;

import java.io.InputStream;

public record SettingsImageFile(
        String originalFileName,
        String contentType,
        long size,
        InputStream inputStream
) {}
