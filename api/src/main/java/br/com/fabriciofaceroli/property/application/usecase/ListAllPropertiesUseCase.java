package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesPort;
import br.com.fabriciofaceroli.property.application.port.in.ListAllPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.out.FindAllPropertiesPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListAllPropertiesUseCase implements ListAllPropertiesPort {

    private final FindAllPropertiesPort findAllPropertiesPort;

    public ListAllPropertiesUseCase(FindAllPropertiesPort findAllPropertiesPort) {
        this.findAllPropertiesPort = findAllPropertiesPort;
    }

    @Override
    public Page<Property> listAll(ListAllPropertiesQuery query) {
        return findAllPropertiesPort.findAll(query);
    }
}
