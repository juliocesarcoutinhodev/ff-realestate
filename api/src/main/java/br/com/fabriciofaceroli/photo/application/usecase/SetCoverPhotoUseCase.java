package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.SetCoverPhotoPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SetCoverPhotoUseCase implements SetCoverPhotoPort {

    private final FindPropertyPort findPropertyPort;
    private final FindPhotosByPropertyPort findPhotosByPropertyPort;
    private final SavePhotosPort savePhotosPort;

    public SetCoverPhotoUseCase(FindPropertyPort findPropertyPort,
                                FindPhotosByPropertyPort findPhotosByPropertyPort,
                                SavePhotosPort savePhotosPort) {
        this.findPropertyPort = findPropertyPort;
        this.findPhotosByPropertyPort = findPhotosByPropertyPort;
        this.savePhotosPort = savePhotosPort;
    }

    @Override
    @Transactional
    public List<PropertyPhoto> setCover(UUID propertyId, UUID photoId) {
        if (!findPropertyPort.existsById(propertyId)) {
            throw new ResourceNotFoundException("Imóvel não encontrado.");
        }

        var photos = findPhotosByPropertyPort.findByPropertyId(propertyId);

        boolean photoExists = photos.stream().anyMatch(p -> photoId.equals(p.id()));
        if (!photoExists) {
            throw new ResourceNotFoundException("Foto não encontrada.");
        }

        var updated = photos.stream()
                .map(p -> new PropertyPhoto(p.id(), p.propertyId(), p.url(), p.fileName(), photoId.equals(p.id()), p.orderIndex()))
                .toList();

        return savePhotosPort.saveAll(updated);
    }
}
