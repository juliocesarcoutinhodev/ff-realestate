package br.com.fabriciofaceroli.property.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.property.application.port.out.DeletePropertyPhotosPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Stub — replace with real photo module adapter once EPIC-04 (Photos) is implemented.
@Component
public class PropertyPhotosStubAdapter implements DeletePropertyPhotosPort {

    @Override
    public void deleteByPropertyId(UUID propertyId) {
        // no-op until photo module is built
    }
}
