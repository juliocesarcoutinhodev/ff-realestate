package br.com.fabriciofaceroli.property.application.port.in;

import br.com.fabriciofaceroli.property.domain.model.Property;
import org.springframework.data.domain.Page;

public interface ListAllPropertiesPort {

    Page<Property> listAll(ListAllPropertiesQuery query);
}
