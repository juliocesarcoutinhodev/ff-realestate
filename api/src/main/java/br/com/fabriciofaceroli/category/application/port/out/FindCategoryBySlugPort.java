package br.com.fabriciofaceroli.category.application.port.out;

import br.com.fabriciofaceroli.category.domain.model.Category;

import java.util.Optional;

public interface FindCategoryBySlugPort {

    Optional<Category> findBySlug(String slug);
}
