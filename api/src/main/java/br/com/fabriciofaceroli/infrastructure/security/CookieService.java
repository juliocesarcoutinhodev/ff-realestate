package br.com.fabriciofaceroli.infrastructure.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

    private static final String COOKIE_NAME = "accessToken";

    private final String domain;
    private final boolean secure;
    private final Duration expiration;

    public CookieService(CookieProperties cookieProperties, JwtProperties jwtProperties) {
        this.domain = cookieProperties.domain();
        this.secure = cookieProperties.secure();
        this.expiration = Duration.ofMillis(jwtProperties.expirationMs());
    }

    public ResponseCookie createAuthCookie(String token) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .domain(domain)
                .maxAge(expiration)
                .build();
    }

    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .domain(domain)
                .maxAge(0)
                .build();
    }
}
