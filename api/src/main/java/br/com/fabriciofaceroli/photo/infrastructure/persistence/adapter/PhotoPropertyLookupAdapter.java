package br.com.fabriciofaceroli.photo.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.photo.application.port.out.FindPropertyPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PhotoPropertyLookupAdapter implements FindPropertyPort {

    private final FindPropertyByIdPort findPropertyByIdPort;

    @Override
    public boolean existsById(UUID id) {
        return findPropertyByIdPort.findById(id).isPresent();
    }
}
