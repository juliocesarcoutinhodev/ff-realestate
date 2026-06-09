package br.com.fabriciofaceroli.photo.application.usecase;

import br.com.fabriciofaceroli.photo.application.port.in.ListPhotosPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListPhotosUseCase implements ListPhotosPort {

    private final FindPropertyPort findPropertyPort;
    private final FindPhotosByPropertyPort findPhotosByPropertyPort;

    public ListPhotosUseCase(FindPropertyPort findPropertyPort,
                             FindPhotosByPropertyPort findPhotosByPropertyPort) {
        this.findPropertyPort = findPropertyPort;
        this.findPhotosByPropertyPort = findPhotosByPropertyPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyPhoto> list(UUID propertyId) {
        if (!findPropertyPort.existsById(propertyId)) {
            throw new ResourceNotFoundException("Imóvel não encontrado.");
        }
        return findPhotosByPropertyPort.findByPropertyId(propertyId);
    }
}
