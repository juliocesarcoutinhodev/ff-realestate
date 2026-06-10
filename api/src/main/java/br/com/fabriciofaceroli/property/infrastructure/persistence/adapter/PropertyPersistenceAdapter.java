package br.com.fabriciofaceroli.property.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.out.CheckPropertySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.DeletePropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.FindActivePropertiesPort;
import br.com.fabriciofaceroli.property.application.port.out.FindAllPropertiesPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyBySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.CategoryInfo;
import br.com.fabriciofaceroli.property.domain.model.DealType;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import br.com.fabriciofaceroli.property.infrastructure.persistence.entity.PropertyEntity;
import br.com.fabriciofaceroli.property.infrastructure.persistence.mapper.PropertyMapper;
import br.com.fabriciofaceroli.property.infrastructure.persistence.repository.PropertyDetailProjection;
import br.com.fabriciofaceroli.property.infrastructure.persistence.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PropertyPersistenceAdapter implements FindActivePropertiesPort, FindAllPropertiesPort, FindPropertyByIdPort, FindPropertyBySlugPort, SavePropertyPort, CheckPropertySlugPort, DeletePropertyByIdPort {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    @Override
    public Page<Property> findActive(ListPropertiesQuery query) {
        Specification<PropertyEntity> spec = (root, q, cb) -> cb.equal(root.get("status"), PropertyStatus.ACTIVE);
        spec = applyCommonFilters(spec, query.categoryId(), query.dealType(), query.featured(), query.city());
        return propertyRepository.findAll(spec, query.pageable()).map(propertyMapper::toProperty);
    }

    @Override
    public Page<Property> findAll(ListAllPropertiesQuery query) {
        Specification<PropertyEntity> spec = (root, q, cb) -> cb.conjunction();
        if (query.status() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), query.status()));
        }
        spec = applyCommonFilters(spec, query.categoryId(), query.dealType(), query.featured(), query.city());
        return propertyRepository.findAll(spec, query.pageable()).map(propertyMapper::toProperty);
    }

    @Override
    public Optional<Property> findById(UUID id) {
        return propertyRepository.findById(id).map(propertyMapper::toProperty);
    }

    @Override
    public void deleteById(UUID id) {
        propertyRepository.deleteById(id);
    }

    @Override
    public Property save(Property property) {
        var entity = propertyMapper.toEntity(property);
        return propertyMapper.toProperty(propertyRepository.save(entity));
    }

    @Override
    public boolean existsBySlug(String slug) {
        return propertyRepository.existsBySlug(slug);
    }

    @Override
    public Optional<PropertyDetail> findBySlug(String slug) {
        return propertyRepository.findActiveBySlug(slug).map(this::toPropertyDetail);
    }

    private Specification<PropertyEntity> applyCommonFilters(
            Specification<PropertyEntity> spec, UUID categoryId,
            DealType dealType, Boolean featured, String city) {

        if (categoryId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("categoryId"), categoryId));
        }
        if (dealType != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("dealType"), dealType));
        }
        if (Boolean.TRUE.equals(featured)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("featured")));
        }
        if (city != null && !city.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
        }
        return spec;
    }

    private PropertyDetail toPropertyDetail(PropertyDetailProjection p) {
        var category = new CategoryInfo(p.getCategoryId(), p.getCategoryName(), p.getCategorySlug());
        return new PropertyDetail(
                p.getId(), p.getTitle(), p.getSlug(), p.getDescription(),
                p.getPrice(), p.getArea(), p.getBedrooms(), p.getSuites(),
                p.getBathrooms(), p.getParkingSpots(), p.getAddress(),
                p.getNeighborhood(), p.getCity(), p.getState(), p.getZipCode(),
                p.getDealType(), p.getFeatured(), p.getStatus(), p.getExternalUrl(),
                category, List.of()
        );
    }
}
