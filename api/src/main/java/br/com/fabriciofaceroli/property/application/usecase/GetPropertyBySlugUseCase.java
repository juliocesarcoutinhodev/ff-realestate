package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.GetPropertyBySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyBySlugPort;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetPropertyBySlugUseCase implements GetPropertyBySlugPort {

    private final FindPropertyBySlugPort findPropertyBySlugPort;

    public GetPropertyBySlugUseCase(FindPropertyBySlugPort findPropertyBySlugPort) {
        this.findPropertyBySlugPort = findPropertyBySlugPort;
    }

    @Override
    public PropertyDetail getBySlug(String slug) {
        return findPropertyBySlugPort.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));
    }
}
