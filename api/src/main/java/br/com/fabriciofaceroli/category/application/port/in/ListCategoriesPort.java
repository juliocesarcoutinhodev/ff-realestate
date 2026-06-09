package br.com.fabriciofaceroli.category.application.port.in;

import br.com.fabriciofaceroli.category.domain.model.Category;

import java.util.List;

public interface ListCategoriesPort {

    List<Category> listAll();
}
