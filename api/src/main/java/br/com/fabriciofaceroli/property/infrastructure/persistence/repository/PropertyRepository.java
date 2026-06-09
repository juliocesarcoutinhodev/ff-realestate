package br.com.fabriciofaceroli.property.infrastructure.persistence.repository;

import br.com.fabriciofaceroli.property.infrastructure.persistence.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<PropertyEntity, UUID>, JpaSpecificationExecutor<PropertyEntity> {

    long countByCategoryId(UUID categoryId);

    @Query(value = """
            SELECT p.id,
                   p.title,
                   p.slug,
                   p.description,
                   p.price,
                   p.area,
                   p.bedrooms,
                   p.suites,
                   p.bathrooms,
                   p.parking_spots   AS parkingSpots,
                   p.address,
                   p.neighborhood,
                   p.city,
                   p.state,
                   p.zip_code        AS zipCode,
                   p.deal_type       AS dealType,
                   p.featured,
                   p.status,
                   p.external_url    AS externalUrl,
                   p.category_id     AS categoryId,
                   c.name            AS categoryName,
                   c.slug            AS categorySlug
            FROM   properties p
                   INNER JOIN categories c ON p.category_id = c.id
            WHERE  p.slug = :slug
              AND  p.status = 'ACTIVE'
            """, nativeQuery = true)
    Optional<PropertyDetailProjection> findActiveBySlug(@Param("slug") String slug);
}
