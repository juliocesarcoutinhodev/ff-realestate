package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.Property;

public interface UpdatePropertyPort {
    Property update(UpdatePropertyCommand command);
}
