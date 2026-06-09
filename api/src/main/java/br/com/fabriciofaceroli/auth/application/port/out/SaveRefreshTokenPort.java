package br.com.fabriciofaceroli.auth.application.port.out;

import java.util.UUID;

public interface SaveRefreshTokenPort {

    void save(UUID userId, RefreshTokenData data);
}
