package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.domain.model.Property;
import org.springframework.data.domain.Page;

public interface FindActivePropertiesPort {

    Page<Property> findActive(ListPropertiesQuery query);
}
