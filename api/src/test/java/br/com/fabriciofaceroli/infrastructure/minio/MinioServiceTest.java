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
import io.minio.messages.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioProperties properties;
    private MinioService minioService;

    @BeforeEach
    void setUp() {
        properties = new MinioProperties(
                "http://localhost:9000",
                "minioadmin",
                "minioadmin",
                "test-bucket",
                "http://localhost:9000"
        );
        minioService = new MinioService(minioClient, properties);
    }

    // ── ensureBucketExists ──────────────────────────────────────────────────

    @Test
    void ensureBucketExists_createsBucket_whenItDoesNotExist() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minioService.ensureBucketExists();

        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void ensureBucketExists_doesNotCreateBucket_whenItAlreadyExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minioService.ensureBucketExists();

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void ensureBucketExists_doesNotThrow_whenMinioIsUnavailable() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenThrow(new RuntimeException("connection refused"));

        minioService.ensureBucketExists();
    }

    // ── uploadFile ──────────────────────────────────────────────────────────

    @Test
    void uploadFile_callsPutObject_withCorrectArgs() throws Exception {
        InputStream stream = new ByteArrayInputStream("data".getBytes());

        minioService.uploadFile("test-bucket", "photo.jpg", stream, "image/jpeg");

        var captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().object()).isEqualTo("photo.jpg");
    }

    @Test
    void uploadFile_throwsBusinessException_whenMinioFails() throws Exception {
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException("timeout"));

        InputStream stream = new ByteArrayInputStream("data".getBytes());

        assertThatThrownBy(() -> minioService.uploadFile("test-bucket", "photo.jpg", stream, "image/jpeg"))
                .isInstanceOf(BusinessException.class);
    }

    // ── deleteFile ──────────────────────────────────────────────────────────

    @Test
    void deleteFile_callsRemoveObject_withCorrectArgs() throws Exception {
        minioService.deleteFile("test-bucket", "photo.jpg");

        var captor = ArgumentCaptor.forClass(RemoveObjectArgs.class);
        verify(minioClient).removeObject(captor.capture());
        assertThat(captor.getValue().bucket()).isEqualTo("test-bucket");
        assertThat(captor.getValue().object()).isEqualTo("photo.jpg");
    }

    @Test
    void deleteFile_throwsResourceNotFoundException_whenObjectDoesNotExist() throws Exception {
        var errorResponse = mock(ErrorResponse.class);
        when(errorResponse.code()).thenReturn("NoSuchKey");
        var exception = mock(ErrorResponseException.class);
        when(exception.errorResponse()).thenReturn(errorResponse);
        doThrow(exception).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThatThrownBy(() -> minioService.deleteFile("test-bucket", "photo.jpg"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteFile_throwsBusinessException_whenMinioFails() throws Exception {
        doThrow(new RuntimeException("timeout")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThatThrownBy(() -> minioService.deleteFile("test-bucket", "photo.jpg"))
                .isInstanceOf(BusinessException.class);
    }

    // ── getPublicUrl ────────────────────────────────────────────────────────

    @Test
    void getPublicUrl_returnsCorrectUrl() {
        var url = minioService.getPublicUrl("test-bucket", "photo.jpg");

        assertThat(url).isEqualTo("http://localhost:9000/test-bucket/photo.jpg");
    }

    @Test
    void getPublicUrl_stripsTrailingSlash_fromPublicUrl() {
        var propsWithSlash = new MinioProperties(
                "http://localhost:9000", "key", "secret", "bucket", "http://localhost:9000/");
        var service = new MinioService(minioClient, propsWithSlash);

        var url = service.getPublicUrl("test-bucket", "photo.jpg");

        assertThat(url).isEqualTo("http://localhost:9000/test-bucket/photo.jpg");
    }
}
