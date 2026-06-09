package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;

public interface GetPropertyBySlugPort {

    PropertyDetail getBySlug(String slug);
}
