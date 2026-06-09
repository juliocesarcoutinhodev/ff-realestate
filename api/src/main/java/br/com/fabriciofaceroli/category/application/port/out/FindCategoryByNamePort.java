package br.com.fabriciofaceroli.category.application.port.out;

import br.com.fabriciofaceroli.category.domain.model.Category;

import java.util.Optional;

public interface FindCategoryByNamePort {

    Optional<Category> findByName(String name);
}
