package br.com.fabriciofaceroli.photo.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.photo.application.port.out.FindPhotosByPropertyPort;
import br.com.fabriciofaceroli.photo.application.port.out.RemovePhotoPort;
import br.com.fabriciofaceroli.photo.application.port.out.SavePhotosPort;
import br.com.fabriciofaceroli.photo.domain.model.PropertyPhoto;
import br.com.fabriciofaceroli.photo.infrastructure.persistence.mapper.PropertyPhotoMapper;
import br.com.fabriciofaceroli.photo.infrastructure.persistence.repository.PropertyPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PhotoPersistenceAdapter implements FindPhotosByPropertyPort, SavePhotosPort, RemovePhotoPort {

    private final PropertyPhotoRepository propertyPhotoRepository;
    private final PropertyPhotoMapper propertyPhotoMapper;

    @Override
    public int countByPropertyId(UUID propertyId) {
        return propertyPhotoRepository.countByPropertyId(propertyId);
    }

    @Override
    public boolean existsCoverByPropertyId(UUID propertyId) {
        return propertyPhotoRepository.existsByPropertyIdAndCoverTrue(propertyId);
    }

    @Override
    public List<PropertyPhoto> findByPropertyId(UUID propertyId) {
        return propertyPhotoRepository.findByPropertyIdOrderByCoverDescOrderIndexAsc(propertyId)
                .stream()
                .map(propertyPhotoMapper::toPropertyPhoto)
                .toList();
    }

    @Override
    public List<PropertyPhoto> saveAll(List<PropertyPhoto> photos) {
        var entities = photos.stream().map(propertyPhotoMapper::toEntity).toList();
        return propertyPhotoRepository.saveAll(entities).stream()
                .map(propertyPhotoMapper::toPropertyPhoto)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        propertyPhotoRepository.deleteById(id);
    }
}
