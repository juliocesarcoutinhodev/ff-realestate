package br.com.fabriciofaceroli.auth.application.port.out;

import java.util.UUID;

public interface RevokeRefreshTokenPort {

    void revoke(UUID tokenId);
}
