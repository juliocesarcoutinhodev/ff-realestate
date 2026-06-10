package br.com.fabriciofaceroli.property.application.port.out;

import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesQuery;
import br.com.fabriciofaceroli.property.domain.model.Property;
import org.springframework.data.domain.Page;

public interface FindAllPropertiesPort {

    Page<Property> findAll(ListAllPropertiesQuery query);
}
