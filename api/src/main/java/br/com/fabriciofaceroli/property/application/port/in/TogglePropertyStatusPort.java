package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.Property;

public interface TogglePropertyStatusPort {
    Property toggle(TogglePropertyStatusCommand command);
}
