package br.com.fabriciofaceroli.category.application.usecase;

import br.com.fabriciofaceroli.category.application.port.in.DeleteCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.CountPropertiesByCategoryPort;
import br.com.fabriciofaceroli.category.application.port.out.DeleteCategoryByIdPort;
import br.com.fabriciofaceroli.category.application.port.out.FindCategoryByIdPort;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteCategoryUseCase implements DeleteCategoryPort {

    private final FindCategoryByIdPort findCategoryByIdPort;
    private final CountPropertiesByCategoryPort countPropertiesByCategoryPort;
    private final DeleteCategoryByIdPort deleteCategoryByIdPort;

    public DeleteCategoryUseCase(FindCategoryByIdPort findCategoryByIdPort,
                                  CountPropertiesByCategoryPort countPropertiesByCategoryPort,
                                  DeleteCategoryByIdPort deleteCategoryByIdPort) {
        this.findCategoryByIdPort = findCategoryByIdPort;
        this.countPropertiesByCategoryPort = countPropertiesByCategoryPort;
        this.deleteCategoryByIdPort = deleteCategoryByIdPort;
    }

    @Override
    public void delete(UUID id) {
        findCategoryByIdPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        if (countPropertiesByCategoryPort.countByCategory(id) > 0) {
            throw new ConflictException("Não é possível excluir uma categoria com imóveis vinculados.");
        }

        deleteCategoryByIdPort.deleteById(id);
    }
}
