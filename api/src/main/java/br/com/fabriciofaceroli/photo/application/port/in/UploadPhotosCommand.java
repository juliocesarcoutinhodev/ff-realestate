package br.com.fabriciofaceroli.photo.application.port.in;

import java.util.List;
import java.util.UUID;

public record UploadPhotosCommand(UUID propertyId, List<PhotoFile> files) {}
