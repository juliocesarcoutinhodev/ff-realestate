package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;

import java.util.Optional;

public interface FindPropertyBySlugPort {

    Optional<PropertyDetail> findBySlug(String slug);
}
