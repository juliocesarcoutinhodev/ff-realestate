package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    private static final UUID TOKEN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void logout_revokesRefreshToken_whenCookieIsPresent() {
        var revokedId = new AtomicReference<UUID>();
        var sut = new LogoutUseCase(
                tokenValue -> Optional.of(validToken()),
                id -> revokedId.set(id)
        );

        sut.logout("valid-refresh-token");

        assertThat(revokedId.get()).isEqualTo(TOKEN_ID);
    }

    @Test
    void logout_doesNotThrow_whenCookieIsAbsent() {
        var sut = new LogoutUseCase(
                tokenValue -> Optional.empty(),
                id -> {}
        );

        sut.logout(null);
    }

    @Test
    void logout_doesNotThrow_whenTokenNotFoundInDatabase() {
        var sut = new LogoutUseCase(
                tokenValue -> Optional.empty(),
                id -> {}
        );

        sut.logout("unknown-token");
    }

    @Test
    void logout_doesNotRevoke_whenRefreshTokenValueIsNull() {
        var revokedId = new AtomicReference<UUID>();
        var sut = new LogoutUseCase(
                tokenValue -> Optional.of(validToken()),
                id -> revokedId.set(id)
        );

        sut.logout(null);

        assertThat(revokedId.get()).isNull();
    }

    private RefreshToken validToken() {
        return new RefreshToken(TOKEN_ID, USER_ID, "valid-refresh-token", Instant.now().plusSeconds(604800), false);
    }
}
