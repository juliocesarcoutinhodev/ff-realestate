package br.com.fabriciofaceroli.property.application.port.in;

import java.util.UUID;

public interface DeletePropertyPort {
    void delete(UUID id);
}
