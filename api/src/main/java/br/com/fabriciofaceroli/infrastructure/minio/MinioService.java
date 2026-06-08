package br.com.fabriciofaceroli.infrastructure.minio;

import br.com.fabriciofaceroli.shared.config.MinioProperties;
import br.com.fabriciofaceroli.shared.exception.BusinessException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class MinioService {

    private static final long PART_SIZE = 10 * 1024 * 1024;

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureBucketExists() {
        try {
            var exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(properties.bucket()).build());

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(properties.bucket()).build());
                log.info("Bucket '{}' criado com sucesso.", properties.bucket());
            } else {
                log.debug("Bucket '{}' já existe.", properties.bucket());
            }
        } catch (Exception e) {
            log.error("Erro ao verificar/criar bucket '{}': {}", properties.bucket(), e.getMessage(), e);
        }
    }

    public void uploadFile(String bucket, String fileName, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(inputStream, -1, PART_SIZE)
                            .contentType(contentType)
                            .build());
            log.debug("Arquivo '{}' enviado para o bucket '{}'.", fileName, bucket);
        } catch (Exception e) {
            log.error("Erro ao enviar arquivo '{}' para o bucket '{}': {}", fileName, bucket, e.getMessage(), e);
            throw new BusinessException("Erro ao fazer upload do arquivo. Tente novamente.");
        }
    }

    public void deleteFile(String bucket, String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build());
            log.debug("Arquivo '{}' removido do bucket '{}'.", fileName, bucket);
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new ResourceNotFoundException("Arquivo não encontrado: " + fileName);
            }
            log.error("Erro ao remover arquivo '{}' do bucket '{}': {}", fileName, bucket, e.getMessage(), e);
            throw new BusinessException("Erro ao remover o arquivo. Tente novamente.");
        } catch (Exception e) {
            log.error("Erro ao remover arquivo '{}' do bucket '{}': {}", fileName, bucket, e.getMessage(), e);
            throw new BusinessException("Erro ao remover o arquivo. Tente novamente.");
        }
    }

    public String getPublicUrl(String bucket, String fileName) {
        var base = properties.publicUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + bucket + "/" + fileName;
    }
}
