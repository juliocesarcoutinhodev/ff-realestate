package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.GetCategoryBySlugPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryBySlugPort;
import br.com.fabriciofaceroli.category.domain.model.Category;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCategoryBySlugUseCase implements GetCategoryBySlugPort {

    private final FindCategoryBySlugPort findCategoryBySlugPort;

    public GetCategoryBySlugUseCase(FindCategoryBySlugPort findCategoryBySlugPort) {
        this.findCategoryBySlugPort = findCategoryBySlugPort;
    }

    @Override
    public Category getBySlug(String slug) {
        return findCategoryBySlugPort.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
    }
}
