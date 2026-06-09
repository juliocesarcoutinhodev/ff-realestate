package br.com.fabriciofaceroli.property.infrastructure.persistence.repository;

import br.com.fabriciofaceroli.property.infrastructure.persistence.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID>, JpaSpecificationExecutor<PropertyEntity> {

    long countByCategoryId(UUID categoryId);
}
