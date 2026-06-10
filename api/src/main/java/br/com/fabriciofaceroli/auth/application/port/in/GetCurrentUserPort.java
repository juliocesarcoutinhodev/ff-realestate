package br.com.fabriciofaceroli.auth.application.port.in;

import br.com.fabriciofaceroli.auth.domain.model.User;

public interface GetCurrentUserPort {

    User getMe(String email);
}
