package br.com.fabriciofaceroli.photo.application.port.out;

import java.util.UUID;

public interface RemovePhotoPort {
    void deleteById(UUID id);
}
