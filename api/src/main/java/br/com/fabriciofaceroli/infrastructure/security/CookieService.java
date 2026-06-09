package br.com.fabriciofaceroli.infrastructure.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

    private static final String ACCESS_COOKIE_NAME = "accessToken";
    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final String domain;
    private final boolean secure;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    public CookieService(CookieProperties cookieProperties, JwtProperties jwtProperties) {
        this.domain = cookieProperties.domain();
        this.secure = cookieProperties.secure();
        this.accessExpiration = Duration.ofSeconds(jwtProperties.accessTokenExpiration());
        this.refreshExpiration = Duration.ofSeconds(jwtProperties.refreshTokenExpiration());
    }

    public ResponseCookie createAuthCookie(String token) {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .domain(domain)
                .maxAge(accessExpiration)
                .build();
    }

    public ResponseCookie createRefreshCookie(String token) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .domain(domain)
                .maxAge(refreshExpiration)
                .build();
    }

    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .domain(domain)
                .maxAge(0)
                .build();
    }
}
