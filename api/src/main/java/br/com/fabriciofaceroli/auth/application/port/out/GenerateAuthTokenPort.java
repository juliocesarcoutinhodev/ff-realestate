package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.User;

public interface GenerateAuthTokenPort {

    String generateToken(User user);
}
