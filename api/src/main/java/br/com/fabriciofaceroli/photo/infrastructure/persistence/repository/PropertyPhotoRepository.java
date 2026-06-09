package br.com.fabriciofaceroli.photo.infrastructure.persistence.repository;

import br.com.fabriciofaceroli.photo.infrastructure.persistence.entity.PropertyPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyPhotoRepository extends JpaRepository<PropertyPhotoEntity, UUID> {
    int countByPropertyId(UUID propertyId);
    boolean existsByPropertyIdAndCoverTrue(UUID propertyId);
    List<PropertyPhotoEntity> findByPropertyIdOrderByOrderIndexAsc(UUID propertyId);
    void deleteByPropertyId(UUID propertyId);
}
