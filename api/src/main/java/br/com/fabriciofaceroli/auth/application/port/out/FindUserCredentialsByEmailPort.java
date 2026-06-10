package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.UserCredentials;

import java.util.Optional;

public interface FindUserCredentialsByEmailPort {

    Optional<UserCredentials> findCredentialsByEmail(String email);
}
