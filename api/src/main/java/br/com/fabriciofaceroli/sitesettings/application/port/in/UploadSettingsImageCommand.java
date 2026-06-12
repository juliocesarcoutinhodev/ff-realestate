package br.com.fabriciofaceroli.sitesettings.application.port.in;

public record UploadSettingsImageCommand(
        SettingsImageType type,
        SettingsImageFile file
) {}
