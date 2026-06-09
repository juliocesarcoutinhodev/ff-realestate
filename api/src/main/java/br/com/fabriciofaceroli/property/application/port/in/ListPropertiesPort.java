package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.Property;
import org.springframework.data.domain.Page;

public interface ListPropertiesPort {

    Page<Property> list(ListPropertiesQuery query);
}
