package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.GetAdminPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyDetailByIdPort;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetAdminPropertyByIdUseCase implements GetAdminPropertyByIdPort {

    private final FindPropertyDetailByIdPort findPropertyDetailByIdPort;

    public GetAdminPropertyByIdUseCase(FindPropertyDetailByIdPort findPropertyDetailByIdPort) {
        this.findPropertyDetailByIdPort = findPropertyDetailByIdPort;
    }

    @Override
    public PropertyDetail getById(UUID id) {
        return findPropertyDetailByIdPort.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));
    }
}
