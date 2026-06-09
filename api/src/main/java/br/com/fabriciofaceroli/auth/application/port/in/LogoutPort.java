package br.com.fabriciofaceroli.auth.application.port.in;

public interface LogoutPort {

    void logout(String refreshTokenValue);
}
