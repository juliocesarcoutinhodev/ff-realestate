package br.com.fabriciofaceroli.auth.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.auth.application.port.out.FindRefreshTokenByValuePort;
import br.com.fabriciofaceroli.auth.application.port.out.RefreshTokenData;
import br.com.fabriciofaceroli.auth.application.port.out.RevokeRefreshTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveRefreshTokenPort;
import br.com.fabriciofaceroli.auth.domain.model.RefreshToken;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.mapper.RefreshTokenMapper;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenPersistenceAdapter implements SaveRefreshTokenPort, FindRefreshTokenByValuePort, RevokeRefreshTokenPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    public RefreshTokenPersistenceAdapter(RefreshTokenRepository refreshTokenRepository,
                                          RefreshTokenMapper refreshTokenMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Override
    public void save(UUID userId, RefreshTokenData data) {
        var entity = RefreshTokenEntity.builder()
                .userId(userId)
                .token(data.tokenValue())
                .expiresAt(data.expiresAt())
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
    }

    @Override
    public Optional<RefreshToken> findByTokenValue(String tokenValue) {
        return refreshTokenRepository.findByToken(tokenValue)
                .map(refreshTokenMapper::toRefreshToken);
    }

    @Override
    public void revoke(UUID tokenId) {
        refreshTokenRepository.revokeById(tokenId);
    }
}
