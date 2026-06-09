package br.com.fabriciofaceroli.property.application.port.out;

import java.util.UUID;

public interface DeletePropertyByIdPort {
    void deleteById(UUID id);
}
