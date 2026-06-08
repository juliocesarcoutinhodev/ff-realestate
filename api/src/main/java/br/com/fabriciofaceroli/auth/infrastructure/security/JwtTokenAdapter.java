package br.com.fabriciofaceroli.auth.infrastructure.security;

import br.com.fabriciofaceroli.auth.application.port.out.GenerateAuthTokenPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.infrastructure.security.JwtService;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenAdapter implements GenerateAuthTokenPort {

    private final JwtService jwtService;

    public JwtTokenAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(User user) {
        return jwtService.generateToken(user.email());
    }
}
