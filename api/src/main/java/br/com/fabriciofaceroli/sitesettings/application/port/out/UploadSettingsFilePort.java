package br.com.fabriciofaceroli.sitesettings.application.port.out;

import java.io.InputStream;

public interface UploadSettingsFilePort {
    String upload(String objectName, String contentType, InputStream inputStream);
}
