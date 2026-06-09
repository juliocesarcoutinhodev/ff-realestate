package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.RefreshToken;

import java.util.Optional;

public interface FindRefreshTokenByValuePort {

    Optional<RefreshToken> findByTokenValue(String tokenValue);
}
