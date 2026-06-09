package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesPort;
import br.com.fabriciofaceroli.property.application.port.in.ListPropertiesQuery;
import br.com.fabriciofaceroli.property.application.port.out.FindActivePropertiesPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListPropertiesUseCase implements ListPropertiesPort {

    private final FindActivePropertiesPort findActivePropertiesPort;

    public ListPropertiesUseCase(FindActivePropertiesPort findActivePropertiesPort) {
        this.findActivePropertiesPort = findActivePropertiesPort;
    }

    @Override
    public Page<Property> list(ListPropertiesQuery query) {
        return findActivePropertiesPort.findActive(query);
    }
}
