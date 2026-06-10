package br.com.fabriciofaceroli.photo.application.port.out;

import java.io.InputStream;

public interface UploadPhotoFilePort {
    String upload(String objectName, String contentType, InputStream inputStream);
}
