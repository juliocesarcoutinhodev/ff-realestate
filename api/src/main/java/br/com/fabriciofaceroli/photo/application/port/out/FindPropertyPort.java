package br.com.fabriciofaceroli.photo.application.port.out;

import java.util.UUID;

public interface FindPropertyPort {
    boolean existsById(UUID id);
}
