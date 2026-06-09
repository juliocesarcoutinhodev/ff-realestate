package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.LoginResult;
import br.com.fabriciofaceroli.auth.application.port.in.RefreshSessionPort;
import br.com.fabriciofaceroli.auth.application.port.out.FindRefreshTokenByValuePort;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserByIdPort;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateAuthTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateRefreshTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.RevokeRefreshTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveRefreshTokenPort;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class RefreshSessionUseCase implements RefreshSessionPort {

    private static final String EXPIRED_SESSION_MESSAGE = "Sessão expirada. Faça login novamente.";

    private final FindRefreshTokenByValuePort findRefreshTokenByValuePort;
    private final RevokeRefreshTokenPort revokeRefreshTokenPort;
    private final FindUserByIdPort findUserByIdPort;
    private final GenerateAuthTokenPort generateAuthTokenPort;
    private final GenerateRefreshTokenPort generateRefreshTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;

    public RefreshSessionUseCase(FindRefreshTokenByValuePort findRefreshTokenByValuePort,
                                 RevokeRefreshTokenPort revokeRefreshTokenPort,
                                 FindUserByIdPort findUserByIdPort,
                                 GenerateAuthTokenPort generateAuthTokenPort,
                                 GenerateRefreshTokenPort generateRefreshTokenPort,
                                 SaveRefreshTokenPort saveRefreshTokenPort) {
        this.findRefreshTokenByValuePort = findRefreshTokenByValuePort;
        this.revokeRefreshTokenPort = revokeRefreshTokenPort;
        this.findUserByIdPort = findUserByIdPort;
        this.generateAuthTokenPort = generateAuthTokenPort;
        this.generateRefreshTokenPort = generateRefreshTokenPort;
        this.saveRefreshTokenPort = saveRefreshTokenPort;
    }

    @Override
    public LoginResult refresh(String refreshTokenValue) {
        var token = findRefreshTokenByValuePort.findByTokenValue(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException(EXPIRED_SESSION_MESSAGE));

        if (token.revoked() || token.expiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException(EXPIRED_SESSION_MESSAGE);
        }

        revokeRefreshTokenPort.revoke(token.id());

        var user = findUserByIdPort.findById(token.userId())
                .orElseThrow(() -> new UnauthorizedException(EXPIRED_SESSION_MESSAGE));

        var newAccessToken = generateAuthTokenPort.generateToken(user);
        var newRefreshData = generateRefreshTokenPort.generateRefreshToken(user);
        saveRefreshTokenPort.save(user.id(), newRefreshData);

        return new LoginResult(user, newAccessToken, newRefreshData.tokenValue());
    }
}
