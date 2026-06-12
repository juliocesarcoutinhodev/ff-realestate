package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosCommand;
import br.com.fabriciofaceroli.photo.application.port.in.UploadPhotosPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.application.port.out.UploadPhotoFilePort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.shared.exception.UnsupportedMediaTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class UploadPhotosUseCase implements UploadPhotosPort {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp");

    private final FindPropertyPort findPropertyPort;
    private final FindPhotosByPropertyPort findPhotosByPropertyPort;
    private final UploadPhotoFilePort uploadPhotoFilePort;
    private final SavePhotosPort savePhotosPort;

    public UploadPhotosUseCase(FindPropertyPort findPropertyPort,
                               FindPhotosByPropertyPort findPhotosByPropertyPort,
                               UploadPhotoFilePort uploadPhotoFilePort,
                               SavePhotosPort savePhotosPort) {
        this.findPropertyPort = findPropertyPort;
        this.findPhotosByPropertyPort = findPhotosByPropertyPort;
        this.uploadPhotoFilePort = uploadPhotoFilePort;
        this.savePhotosPort = savePhotosPort;
    }

    @Override
    @Transactional
    public List<PropertyPhoto> upload(UploadPhotosCommand command) {
        if (!findPropertyPort.existsById(command.propertyId())) {
            throw new ResourceNotFoundException("Imóvel não encontrado.");
        }

        for (var file : command.files()) {
            log.debug("Validando arquivo: fileName='{}', contentType='{}'", file.originalFileName(), file.contentType());
            if (!isAllowedExtension(file.originalFileName()) && !isAllowedContentType(file.contentType())) {
                log.warn("Formato rejeitado: fileName='{}', contentType='{}'", file.originalFileName(), file.contentType());
                throw new UnsupportedMediaTypeException("Formato de arquivo não suportado. Formatos aceitos: jpg, jpeg, png, webp.");
            }
        }

        var nextOrder = findPhotosByPropertyPort.countByPropertyId(command.propertyId());
        var hasCover = findPhotosByPropertyPort.existsCoverByPropertyId(command.propertyId());

        var photos = new ArrayList<PropertyPhoto>();
        for (int i = 0; i < command.files().size(); i++) {
            var file = command.files().get(i);
            var safeName = file.originalFileName() != null ? file.originalFileName() : "upload";
            var objectName = "properties/" + command.propertyId() + "/" + UUID.randomUUID() + "-" + safeName;
            var url = uploadPhotoFilePort.upload(objectName, file.contentType(), file.inputStream());
            var isCover = !hasCover && i == 0;
            photos.add(new PropertyPhoto(null, command.propertyId(), url, file.originalFileName(), isCover, nextOrder + i));
        }

        savePhotosPort.saveAll(photos);
        return findPhotosByPropertyPort.findByPropertyId(command.propertyId());
    }

    private boolean isAllowedExtension(String fileName) {
        if (fileName == null) return false;
        var dot = fileName.lastIndexOf('.');
        if (dot < 0) return false;
        return ALLOWED_EXTENSIONS.contains(fileName.substring(dot + 1).toLowerCase());
    }

    private boolean isAllowedContentType(String contentType) {
        if (contentType == null) return false;
        return ALLOWED_CONTENT_TYPES.contains(contentType);
    }
}
