package br.com.fabriciofaceroli.shared.security;

import br.com.fabriciofaceroli.shared.config.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtService(JwtProperties properties) {
        var key = properties.secret().getBytes(StandardCharsets.UTF_8);
        this.algorithm = Algorithm.HMAC256(key);
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = properties.expirationMs();
    }

    public String generateToken(UserDetails userDetails) {
        var now = Instant.now();
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(expirationMs))
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        return verifier.verify(token).getSubject();
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
