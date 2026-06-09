package br.com.fabriciofaceroli.category.application.port.out;

import java.util.UUID;

public interface DeleteCategoryByIdPort {

    void deleteById(UUID id);
}
