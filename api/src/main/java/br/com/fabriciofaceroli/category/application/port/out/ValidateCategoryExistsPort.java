package br.com.fabriciofaceroli.category.application.port.out;

import java.util.UUID;

public interface ValidateCategoryExistsPort {

    boolean exists(UUID categoryId);
}
