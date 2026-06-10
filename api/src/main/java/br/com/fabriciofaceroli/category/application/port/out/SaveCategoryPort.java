package br.com.fabriciofaceroli.category.application.port.out;

import br.com.fabriciofaceroli.category.domain.model.Category;

public interface SaveCategoryPort {

    Category save(Category category);
}
