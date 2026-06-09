package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.User;

public interface GenerateRefreshTokenPort {

    RefreshTokenData generateRefreshToken(User user);
}
