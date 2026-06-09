package br.com.fabriciofaceroli.auth.application.port.in;

public interface RefreshSessionPort {

    LoginResult refresh(String refreshTokenValue);
}
