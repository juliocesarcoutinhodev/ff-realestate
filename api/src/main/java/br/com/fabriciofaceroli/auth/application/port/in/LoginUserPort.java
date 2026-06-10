package br.com.fabriciofaceroli.auth.application.port.in;

public interface LoginUserPort {

    LoginResult login(LoginCommand command);
}
