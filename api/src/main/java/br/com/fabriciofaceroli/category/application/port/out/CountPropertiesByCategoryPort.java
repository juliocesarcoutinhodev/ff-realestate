package br.com.fabriciofaceroli.category.application.port.out;

import java.util.UUID;

public interface CountPropertiesByCategoryPort {

    long countByCategory(UUID categoryId);
}
