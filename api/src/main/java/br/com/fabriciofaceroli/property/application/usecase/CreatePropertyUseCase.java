package br.com.fabriciofaceroli.property.application.usecase;

import br.com.fabriciofaceroli.category.application.port.out.ValidateCategoryExistsPort;
import br.com.fabriciofaceroli.property.application.port.in.CreatePropertyCommand;
import br.com.fabriciofaceroli.property.application.port.in.CreatePropertyPort;
import br.com.fabriciofaceroli.property.application.port.out.CheckPropertySlugPort;
import br.com.fabriciofaceroli.property.application.port.out.SavePropertyPort;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

@Service
@Transactional
public class CreatePropertyUseCase implements CreatePropertyPort {

    private final ValidateCategoryExistsPort validateCategoryExistsPort;
    private final CheckPropertySlugPort checkPropertySlugPort;
    private final SavePropertyPort savePropertyPort;

    public CreatePropertyUseCase(ValidateCategoryExistsPort validateCategoryExistsPort,
                                  CheckPropertySlugPort checkPropertySlugPort,
                                  SavePropertyPort savePropertyPort) {
        this.validateCategoryExistsPort = validateCategoryExistsPort;
        this.checkPropertySlugPort = checkPropertySlugPort;
        this.savePropertyPort = savePropertyPort;
    }

    @Override
    public Property create(CreatePropertyCommand command) {
        if (!validateCategoryExistsPort.exists(command.categoryId())) {
            throw new ResourceNotFoundException("Categoria não encontrada.");
        }

        var slug = uniqueSlug(generateSlug(command.title()));

        var property = new Property(
                null,
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
                "ACTIVE",
                command.externalUrl(),
                command.categoryId()
        );

        return savePropertyPort.save(property);
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
