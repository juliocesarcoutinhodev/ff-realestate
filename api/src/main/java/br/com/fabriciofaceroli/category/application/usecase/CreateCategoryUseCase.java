package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.CreateCategoryCommand;
import br.com.fabriciofaceroli.category.application.port.in.CreateCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort;
import br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

@Service
@Transactional
public class CreateCategoryUseCase implements CreateCategoryPort {

    private final FindCategoryByNamePort findCategoryByNamePort;
    private final SaveCategoryPort saveCategoryPort;

    public CreateCategoryUseCase(FindCategoryByNamePort findCategoryByNamePort,
                                  SaveCategoryPort saveCategoryPort) {
        this.findCategoryByNamePort = findCategoryByNamePort;
        this.saveCategoryPort = saveCategoryPort;
    }

    @Override
    public Category create(CreateCategoryCommand command) {
        findCategoryByNamePort.findByName(command.name())
                .ifPresent(c -> { throw new ConflictException("Já existe uma categoria com esse nome."); });

        var slug = generateSlug(command.name());
        var category = new Category(null, command.name(), slug, command.description());
        return saveCategoryPort.save(category);
    }

    private String generateSlug(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}
