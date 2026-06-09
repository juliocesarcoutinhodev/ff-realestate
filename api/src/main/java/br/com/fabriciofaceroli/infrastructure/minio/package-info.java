/**
 * Named interface exposing MinIO storage service to domain module adapters.
 * Intentional cross-module access: photo.infrastructure.storage.MinioPhotoAdapter
 * uses MinioService and MinioProperties from this package.
 */
@NamedInterface("minio")
package br.com.fabriciofaceroli.infrastructure.minio;

import org.springframework.modulith.NamedInterface;
