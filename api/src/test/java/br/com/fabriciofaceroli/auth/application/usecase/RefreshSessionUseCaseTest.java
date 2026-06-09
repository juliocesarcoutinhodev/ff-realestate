package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.out.RefreshTokenData;
import br.com.fabriciofaceroli.auth.domain.model.RefreshToken;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class RefreshSessionUseCaseTest {

    private static final String EXPIRED_SESSION_MESSAGE = "Sessão expirada. Faça login novamente.";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TOKEN_ID = UUID.randomUUID();

    private RefreshSessionUseCase useCase(RefreshToken token, User user) {
        var newRefreshData = new RefreshTokenData("new-refresh-token", Instant.now().plusSeconds(604800));
        return new RefreshSessionUseCase(
                tokenValue -> Optional.ofNullable(token),
                id -> {},
                id -> Optional.ofNullable(user),
                u -> "new-access-token",
                u -> newRefreshData,
                (userId, data) -> {}
        );
    }

    @Test
    void refresh_shouldReturnNewTokenPairAndUser_whenTokenIsValid() {
        var sut = useCase(validToken(), user());

        var result = sut.refresh("valid-refresh-token");

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.user().email()).isEqualTo("fabricio@email.com");
    }

    @Test
    void refresh_shouldRevokeCurrentToken_whenTokenIsValid() {
        var revokedId = new AtomicReference<UUID>();
        var newRefreshData = new RefreshTokenData("new-refresh-token", Instant.now().plusSeconds(604800));
        var sut = new RefreshSessionUseCase(
                tokenValue -> Optional.of(validToken()),
                id -> revokedId.set(id),
                id -> Optional.of(user()),
                u -> "new-access-token",
                u -> newRefreshData,
                (userId, data) -> {}
        );

        sut.refresh("valid-refresh-token");

        assertThat(revokedId.get()).isEqualTo(TOKEN_ID);
    }

    @Test
    void refresh_shouldPersistNewRefreshToken_whenTokenIsValid() {
        var savedUserId = new AtomicReference<UUID>();
        var newRefreshData = new RefreshTokenData("new-refresh-token", Instant.now().plusSeconds(604800));
        var sut = new RefreshSessionUseCase(
                tokenValue -> Optional.of(validToken()),
                id -> {},
                id -> Optional.of(user()),
                u -> "new-access-token",
                u -> newRefreshData,
                (userId, data) -> savedUserId.set(userId)
        );

        sut.refresh("valid-refresh-token");

        assertThat(savedUserId.get()).isEqualTo(USER_ID);
    }

    @Test
    void refresh_shouldThrowUnauthorizedException_whenTokenNotFound() {
        var sut = new RefreshSessionUseCase(
                tokenValue -> Optional.empty(),
                id -> {},
                id -> Optional.of(user()),
                u -> "new-access-token",
                u -> new RefreshTokenData("new-refresh-token", Instant.now().plusSeconds(604800)),
                (userId, data) -> {}
        );

        assertThatThrownBy(() -> sut.refresh("unknown-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(EXPIRED_SESSION_MESSAGE);
    }

    @Test
    void refresh_shouldThrowUnauthorizedException_whenTokenIsRevoked() {
        var revokedToken = new RefreshToken(TOKEN_ID, USER_ID, "revoked-token", Instant.now().plusSeconds(604800), true);
        var sut = useCase(revokedToken, user());

        assertThatThrownBy(() -> sut.refresh("revoked-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(EXPIRED_SESSION_MESSAGE);
    }

    @Test
    void refresh_shouldThrowUnauthorizedException_whenTokenIsExpired() {
        var expiredToken = new RefreshToken(TOKEN_ID, USER_ID, "expired-token", Instant.now().minusSeconds(1), false);
        var sut = useCase(expiredToken, user());

        assertThatThrownBy(() -> sut.refresh("expired-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(EXPIRED_SESSION_MESSAGE);
    }

    private RefreshToken validToken() {
        return new RefreshToken(TOKEN_ID, USER_ID, "valid-refresh-token", Instant.now().plusSeconds(604800), false);
    }

    private User user() {
        return new User(USER_ID, "Fabrício Faceroli", "fabricio@email.com", UserRole.ADMIN.name(), true);
    }
}
