package br.com.fabriciofaceroli.auth.application.port.out;

public interface AuthCookiePort {

    String createAuthCookie(String token);

    String createRefreshCookie(String token);

    String createLogoutCookie();
}
