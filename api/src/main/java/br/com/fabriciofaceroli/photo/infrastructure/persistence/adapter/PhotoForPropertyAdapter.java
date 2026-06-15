package br.com.fabriciofaceroli.photo.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.photo.infrastructure.persistence.repository.PropertyPhotoRepository;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyPhotosPort;
import br.com.fabriciofaceroli.property.domain.model.PhotoSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PhotoForPropertyAdapter implements FindPropertyPhotosPort {

    private final PropertyPhotoRepository propertyPhotoRepository;

    @Override
    public List<PhotoSummary> findByPropertyId(UUID propertyId) {
        return propertyPhotoRepository.findByPropertyIdOrderByCoverDescOrderIndexAsc(propertyId)
                .stream()
                .map(e -> new PhotoSummary(e.getId(), e.getUrl(), e.getOrderIndex(), e.isCover()))
                .toList();
    }
}
