package br.com.fabriciofaceroli.photo.infrastructure.storage;

import br.com.fabriciofaceroli.infrastructure.minio.MinioProperties;
import br.com.fabriciofaceroli.infrastructure.minio.MinioService;
import br.com.fabriciofaceroli.photo.application.port.out.UploadPhotoFilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class MinioPhotoAdapter implements UploadPhotoFilePort {

    private final MinioService minioService;
    private final MinioProperties minioProperties;

    @Override
    public String upload(String objectName, String contentType, InputStream inputStream) {
        minioService.uploadFile(minioProperties.bucket(), objectName, inputStream, contentType);
        return minioService.getPublicUrl(minioProperties.bucket(), objectName);
    }
}
