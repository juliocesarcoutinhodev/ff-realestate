package br.com.fabriciofaceroli.auth.application.port.out;

import br.com.fabriciofaceroli.auth.domain.model.User;

public interface SaveUserPort {

    User save(User user, String encodedPassword);
}
