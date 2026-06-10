package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.LogoutPort;
import br.com.fabriciofaceroli.auth.application.port.out.FindRefreshTokenByValuePort;
import br.com.fabriciofaceroli.auth.application.port.out.RevokeRefreshTokenPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogoutUseCase implements LogoutPort {

    private final FindRefreshTokenByValuePort findRefreshTokenByValuePort;
    private final RevokeRefreshTokenPort revokeRefreshTokenPort;

    public LogoutUseCase(FindRefreshTokenByValuePort findRefreshTokenByValuePort,
                         RevokeRefreshTokenPort revokeRefreshTokenPort) {
        this.findRefreshTokenByValuePort = findRefreshTokenByValuePort;
        this.revokeRefreshTokenPort = revokeRefreshTokenPort;
    }

    @Override
    public void logout(String refreshTokenValue) {
        if (refreshTokenValue == null) return;
        findRefreshTokenByValuePort.findByTokenValue(refreshTokenValue)
                .ifPresent(token -> revokeRefreshTokenPort.revoke(token.id()));
    }
}
