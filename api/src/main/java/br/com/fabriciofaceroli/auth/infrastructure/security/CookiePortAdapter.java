package br.com.fabriciofaceroli.auth.infrastructure.security;

import br.com.fabriciofaceroli.auth.application.port.out.AuthCookiePort;
import br.com.fabriciofaceroli.infrastructure.security.CookieService;
import org.springframework.stereotype.Component;

@Component
public class CookiePortAdapter implements AuthCookiePort {

    private final CookieService cookieService;

    public CookiePortAdapter(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    @Override
    public String createAuthCookie(String token) {
        return cookieService.createAuthCookie(token).toString();
    }

    @Override
    public String createRefreshCookie(String token) {
        return cookieService.createRefreshCookie(token).toString();
    }

    @Override
    public String createLogoutCookie() {
        return cookieService.createLogoutCookie().toString();
    }

    @Override
    public String createLogoutRefreshCookie() {
        return cookieService.createLogoutRefreshCookie().toString();
    }
}
