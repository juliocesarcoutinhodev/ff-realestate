package br.com.fabriciofaceroli.property.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.out.FindActivePropertiesPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import br.com.fabriciofaceroli.property.infrastructure.persistence.entity.PropertyEntity;
import br.com.fabriciofaceroli.property.infrastructure.persistence.mapper.PropertyMapper;
import br.com.fabriciofaceroli.property.infrastructure.persistence.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyPersistenceAdapter implements FindActivePropertiesPort {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    @Override
    public Page<Property> findActive(ListPropertiesQuery query) {
        Specification<PropertyEntity> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("status"), PropertyStatus.ACTIVE)
        );

        if (query.categoryId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("categoryId"), query.categoryId()));
        }
        if (query.dealType() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("dealType"), query.dealType()));
        }
        if (Boolean.TRUE.equals(query.featured())) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("featured")));
        }
        if (query.city() != null && !query.city().isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("city")), "%" + query.city().toLowerCase() + "%"));
        }

        return propertyRepository.findAll(spec, query.pageable())
                .map(propertyMapper::toProperty);
    }
}
