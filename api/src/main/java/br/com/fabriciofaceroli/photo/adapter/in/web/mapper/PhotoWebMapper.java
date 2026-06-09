package br.com.fabriciofaceroli.photo.adapter.in.web.mapper;

import br.com.fabriciofaceroli.photo.adapter.in.web.dto.PhotoUploadResponse;
import br.com.fabriciofaceroli.photo.application.port.in.PhotoFile;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PhotoWebMapper {

    PhotoUploadResponse toResponse(PropertyPhoto photo);

    List<PhotoUploadResponse> toResponseList(List<PropertyPhoto> photos);

    default List<PhotoFile> toPhotoFiles(List<MultipartFile> files) {
        return files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(this::toPhotoFile)
                .toList();
    }

    default PhotoFile toPhotoFile(MultipartFile file) {
        try {
            return new PhotoFile(file.getOriginalFilename(), file.getContentType(), file.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
