package br.com.fabriciofaceroli.category.application.port.out;

import br.com.fabriciofaceroli.category.domain.model.Category;

import java.util.List;

public interface FindAllCategoriesPort {

    List<Category> findAll();
}
