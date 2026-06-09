package br.com.fabriciofaceroli.property.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.category.application.port.out.CountPropertiesByCategoryPort;
import br.com.fabriciofaceroli.property.infrastructure.persistence.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PropertyCountAdapter implements CountPropertiesByCategoryPort {

    private final PropertyRepository propertyRepository;

    @Override
    public long countByCategory(UUID categoryId) {
        return propertyRepository.countByCategoryId(categoryId);
    }
}
