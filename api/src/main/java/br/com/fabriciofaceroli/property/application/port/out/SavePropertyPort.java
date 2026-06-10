package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.domain.model.Property;

public interface SavePropertyPort {

    Property save(Property property);
}
