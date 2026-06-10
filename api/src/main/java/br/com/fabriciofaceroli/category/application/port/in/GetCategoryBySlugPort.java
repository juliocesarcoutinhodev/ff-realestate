package br.com.fabriciofaceroli.category.application.port.in;

import br.com.fabriciofaceroli.category.domain.model.Category;

public interface GetCategoryBySlugPort {

    Category getBySlug(String slug);
}
