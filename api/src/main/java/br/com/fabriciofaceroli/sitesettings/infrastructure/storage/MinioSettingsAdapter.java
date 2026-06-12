package br.com.fabriciofaceroli.sitesettings.infrastructure.storage;

import br.com.fabriciofaceroli.infrastructure.minio.MinioProperties;
import br.com.fabriciofaceroli.infrastructure.minio.MinioService;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.sitesettings.application.port.out.DeleteSettingsFilePort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.UploadSettingsFilePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioSettingsAdapter implements UploadSettingsFilePort, DeleteSettingsFilePort {

    private final MinioService minioService;
    private final MinioProperties minioProperties;

    @Override
    public String upload(String objectName, String contentType, InputStream inputStream) {
        minioService.uploadFile(minioProperties.bucket(), objectName, inputStream, contentType);
        return minioService.getPublicUrl(minioProperties.bucket(), objectName);
    }

    @Override
    public void delete(String url) {
        var prefix = minioProperties.publicUrl().replaceAll("/$", "") + "/" + minioProperties.bucket() + "/";
        if (!url.startsWith(prefix)) {
            log.warn("URL de imagem não pertence ao bucket configurado, ignorando deleção: {}", url);
            return;
        }
        var objectName = url.substring(prefix.length());
        try {
            minioService.deleteFile(minioProperties.bucket(), objectName);
        } catch (ResourceNotFoundException e) {
            log.warn("Arquivo não encontrado no MinIO durante substituição, ignorando: {}", objectName);
        }
    }
}
