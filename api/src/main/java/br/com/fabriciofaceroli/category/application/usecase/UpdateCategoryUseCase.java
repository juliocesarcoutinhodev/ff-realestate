package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.UpdateCategoryCommand;
import br.com.fabriciofaceroli.category.application.port.in.UpdateCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByIdPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByNamePort;
import br.com.fabriciofaceroli.category.application.port.out.SaveCategoryPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

@Service
@Transactional
public class UpdateCategoryUseCase implements UpdateCategoryPort {

    private final FindCategoryByIdPort findCategoryByIdPort;
    private final FindCategoryByNamePort findCategoryByNamePort;
    private final SaveCategoryPort saveCategoryPort;

    public UpdateCategoryUseCase(FindCategoryByIdPort findCategoryByIdPort,
                                  FindCategoryByNamePort findCategoryByNamePort,
                                  SaveCategoryPort saveCategoryPort) {
        this.findCategoryByIdPort = findCategoryByIdPort;
        this.findCategoryByNamePort = findCategoryByNamePort;
        this.saveCategoryPort = saveCategoryPort;
    }

    @Override
    public Category update(UpdateCategoryCommand command) {
        findCategoryByIdPort.findById(command.id())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        findCategoryByNamePort.findByName(command.name())
                .filter(existing -> !existing.id().equals(command.id()))
                .ifPresent(c -> { throw new ConflictException("Já existe uma categoria com esse nome."); });

        var slug = generateSlug(command.name());
        var updated = new Category(command.id(), command.name(), slug, command.description());
        return saveCategoryPort.save(updated);
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
