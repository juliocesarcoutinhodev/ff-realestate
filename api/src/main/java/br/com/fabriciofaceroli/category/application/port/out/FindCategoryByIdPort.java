package br.com.fabriciofaceroli.category.application.port.out;

import br.com.fabriciofaceroli.category.domain.model.Category;

import java.util.Optional;
import java.util.UUID;

public interface FindCategoryByIdPort {

    Optional<Category> findById(UUID id);
}
