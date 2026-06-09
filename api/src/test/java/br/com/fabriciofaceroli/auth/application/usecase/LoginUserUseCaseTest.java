package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.LoginCommand;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateAuthTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateRefreshTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.RefreshTokenData;
import br.com.fabriciofaceroli.auth.application.port.out.SaveRefreshTokenPort;
import br.com.fabriciofaceroli.auth.domain.model.UserCredentials;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.ForbiddenException;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    private static final String INVALID_CREDENTIALS_MESSAGE = "E-mail ou senha inválidos.";

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void login_shouldReturnAccessAndRefreshTokens_whenCredentialsAreValid() {
        var refreshData = new RefreshTokenData("refresh-token", Instant.now().plusSeconds(604800));
        var sut = new LoginUserUseCase(
                email -> Optional.of(credentials(true)),
                user -> "access-token",
                user -> refreshData,
                (userId, data) -> {},
                passwordEncoder
        );

        var result = sut.login(new LoginCommand("fabricio@email.com", "senhaSegura123"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.user().email()).isEqualTo("fabricio@email.com");
        assertThat(result.user().role()).isEqualTo(UserRole.ADMIN.name());
    }

    @Test
    void login_shouldPersistRefreshToken_whenCredentialsAreValid() {
        var savedUserId = new AtomicReference<UUID>();
        var refreshData = new RefreshTokenData("refresh-token", Instant.now().plusSeconds(604800));
        var sut = new LoginUserUseCase(
                email -> Optional.of(credentials(true)),
                user -> "access-token",
                user -> refreshData,
                (id, data) -> savedUserId.set(id),
                passwordEncoder
        );

        var result = sut.login(new LoginCommand("fabricio@email.com", "senhaSegura123"));

        assertThat(savedUserId.get()).isEqualTo(result.user().id());
    }

    @Test
    void login_shouldThrowUnauthorizedException_whenEmailNotFound() {
        var sut = new LoginUserUseCase(
                email -> Optional.empty(),
                user -> "access-token",
                user -> new RefreshTokenData("refresh-token", Instant.now()),
                (userId, data) -> {},
                passwordEncoder
        );

        assertThatThrownBy(() -> sut.login(new LoginCommand("fabricio@email.com", "senhaSegura123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(INVALID_CREDENTIALS_MESSAGE);
    }

    @Test
    void login_shouldThrowUnauthorizedException_whenPasswordDoesNotMatch() {
        var sut = new LoginUserUseCase(
                email -> Optional.of(credentials(true)),
                user -> "access-token",
                user -> new RefreshTokenData("refresh-token", Instant.now()),
                (userId, data) -> {},
                passwordEncoder
        );

        assertThatThrownBy(() -> sut.login(new LoginCommand("fabricio@email.com", "senhaErrada123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(INVALID_CREDENTIALS_MESSAGE);
    }

    @Test
    void login_shouldThrowForbiddenException_whenUserIsInactive() {
        var sut = new LoginUserUseCase(
                email -> Optional.of(credentials(false)),
                user -> "access-token",
                user -> new RefreshTokenData("refresh-token", Instant.now()),
                (userId, data) -> {},
                passwordEncoder
        );

        assertThatThrownBy(() -> sut.login(new LoginCommand("fabricio@email.com", "senhaSegura123")))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Usuário inativo, contate o administrador.");
    }

    private UserCredentials credentials(boolean active) {
        return new UserCredentials(
                UUID.randomUUID(),
                "Fabrício Faceroli",
                "fabricio@email.com",
                passwordEncoder.encode("senhaSegura123"),
                UserRole.ADMIN.name(),
                active
        );
    }
}
