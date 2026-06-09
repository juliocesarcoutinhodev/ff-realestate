package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.property.application.port.in.TogglePropertyStatusCommand;
import br.com.fabriciofaceroli.property.application.port.in.TogglePropertyStatusPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TogglePropertyStatusUseCase implements TogglePropertyStatusPort {

    private final FindPropertyByIdPort findPropertyByIdPort;
    private final SavePropertyPort savePropertyPort;

    public TogglePropertyStatusUseCase(FindPropertyByIdPort findPropertyByIdPort,
                                        SavePropertyPort savePropertyPort) {
        this.findPropertyByIdPort = findPropertyByIdPort;
        this.savePropertyPort = savePropertyPort;
    }

    @Override
    public Property toggle(TogglePropertyStatusCommand command) {
        var existing = findPropertyByIdPort.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));

        var updated = new Property(
                existing.id(), existing.title(), existing.slug(), existing.description(),
                existing.price(), existing.area(), existing.bedrooms(), existing.suites(),
                existing.bathrooms(), existing.parkingSpots(), existing.address(),
                existing.neighborhood(), existing.city(), existing.state(), existing.zipCode(),
                existing.dealType(), existing.featured(), command.status().name(),
                existing.externalUrl(), existing.categoryId()
        );

        return savePropertyPort.save(updated);
    }
}
