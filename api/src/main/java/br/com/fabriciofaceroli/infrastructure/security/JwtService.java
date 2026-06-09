package br.com.fabriciofaceroli.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtService(JwtProperties properties) {
        var key = properties.secret().getBytes(StandardCharsets.UTF_8);
        this.algorithm = Algorithm.HMAC256(key);
        this.verifier = JWT.require(algorithm).build();
        this.accessTokenExpiration = Duration.ofSeconds(properties.accessTokenExpiration());
        this.refreshTokenExpiration = Duration.ofSeconds(properties.refreshTokenExpiration());
    }

    public String generateToken(String subject, String role) {
        var now = Instant.now();
        return JWT.create()
                .withSubject(subject)
                .withClaim("role", role)
                .withClaim("type", "access")
                .withIssuedAt(now)
                .withExpiresAt(now.plus(accessTokenExpiration))
                .sign(algorithm);
    }

    public String generateRefreshToken(String subject) {
        var now = Instant.now();
        return JWT.create()
                .withSubject(subject)
                .withClaim("type", "refresh")
                .withIssuedAt(now)
                .withExpiresAt(now.plus(refreshTokenExpiration))
                .sign(algorithm);
    }

    public Duration getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public String extractEmail(String token) {
        return verifier.verify(token).getSubject();
    }

    public String extractRole(String token) {
        return verifier.verify(token).getClaim("role").asString();
    }

    public boolean isTokenValid(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
