package br.com.fabriciofaceroli.auth.infrastructure.security;

import br.com.fabriciofaceroli.auth.application.port.out.GenerateRefreshTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.RefreshTokenData;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.infrastructure.security.JwtService;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtRefreshTokenAdapter implements GenerateRefreshTokenPort {

    private final JwtService jwtService;

    public JwtRefreshTokenAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public RefreshTokenData generateRefreshToken(User user) {
        var expiresAt = Instant.now().plus(jwtService.getRefreshTokenExpiration());
        var tokenValue = jwtService.generateRefreshToken(user.email());
        return new RefreshTokenData(tokenValue, expiresAt);
    }
}
