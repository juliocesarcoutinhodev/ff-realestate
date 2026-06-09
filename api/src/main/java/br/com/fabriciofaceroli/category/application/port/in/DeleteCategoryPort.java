package br.com.fabriciofaceroli.category.application.port.in;

import java.util.UUID;

public interface DeleteCategoryPort {

    void delete(UUID id);
}
