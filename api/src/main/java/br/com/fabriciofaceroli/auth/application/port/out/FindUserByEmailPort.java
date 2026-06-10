package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.User;

import java.util.Optional;

public interface FindUserByEmailPort {

    Optional<User> findByEmail(String email);
}
