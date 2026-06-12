package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.category.application.port.out.ValidateCategoryExistsPort;
import br.com.fabriciofaceroli.property.application.port.in.UpdatePropertyCommand;
import br.com.fabriciofaceroli.property.application.port.in.UpdatePropertyPort;
import br.com.fabriciofaceroli.property.application.port.out.CheckPropertySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.FindPropertyByIdPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.domain.model.PropertyStatus;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

@Service
@Transactional
public class UpdatePropertyUseCase implements UpdatePropertyPort {

    private final FindPropertyByIdPort findPropertyByIdPort;
    private final ValidateCategoryExistsPort validateCategoryExistsPort;
    private final CheckPropertySlugPort checkPropertySlugPort;
    private final SavePropertyPort savePropertyPort;

    public UpdatePropertyUseCase(FindPropertyByIdPort findPropertyByIdPort,
                                  ValidateCategoryExistsPort validateCategoryExistsPort,
                                  CheckPropertySlugPort checkPropertySlugPort,
                                  SavePropertyPort savePropertyPort) {
        this.findPropertyByIdPort = findPropertyByIdPort;
        this.validateCategoryExistsPort = validateCategoryExistsPort;
        this.checkPropertySlugPort = checkPropertySlugPort;
        this.savePropertyPort = savePropertyPort;
    }

    @Override
    public Property update(UpdatePropertyCommand command) {
        var existing = findPropertyByIdPort.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));

        if (!validateCategoryExistsPort.exists(command.categoryId())) {
            throw new ResourceNotFoundException("Categoria não encontrada.");
        }

        var newBaseSlug = generateSlug(command.title());
        var slug = newBaseSlug.equals(existing.slug()) ? existing.slug() : uniqueSlug(newBaseSlug);

        var status = command.status() != null ? command.status() : PropertyStatus.valueOf(existing.status());

        return savePropertyPort.save(new Property(
                existing.id(),
                command.title(),
                slug,
                command.description(),
                command.price(),
                command.area(),
                command.bedrooms(),
                command.suites(),
                command.bathrooms(),
                command.parkingSpots(),
                command.address(),
                command.neighborhood(),
                command.city(),
                command.state(),
                command.zipCode(),
                command.dealType().name(),
                Boolean.TRUE.equals(command.featured()),
                status.name(),
                command.externalUrl(),
                command.categoryId(),
                null
        ));
    }

    private String uniqueSlug(String base) {
        var candidate = base;
        var counter = 2;
        while (checkPropertySlugPort.existsBySlug(candidate)) {
            candidate = base + "-" + counter++;
        }
        return candidate;
    }

    private String generateSlug(String title) {
        return Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}
