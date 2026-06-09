package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface FindUserByIdPort {

    Optional<User> findById(UUID id);
}
