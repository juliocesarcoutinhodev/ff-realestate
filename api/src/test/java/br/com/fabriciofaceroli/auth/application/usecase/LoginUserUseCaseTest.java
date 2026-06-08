package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.LoginCommand;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateAuthTokenPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserCredentials;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.ForbiddenException;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginUserUseCaseTest {

    private static final String INVALID_CREDENTIALS_MESSAGE = "E-mail ou senha inválidos.";

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void login_returnsUserAndToken_whenCredentialsAreValid() {
        var credentials = credentials(true);
        GenerateAuthTokenPort tokenPort = user -> "jwt-token";
        var useCase = new LoginUserUseCase(email -> Optional.of(credentials), tokenPort, passwordEncoder);

        var result = useCase.login(new LoginCommand("fabricio@email.com", "senhaSegura123"));

        assertEquals("jwt-token", result.token());
        assertEquals(credentials.id(), result.user().id());
        assertEquals(credentials.name(), result.user().name());
        assertEquals(credentials.email(), result.user().email());
        assertEquals(UserRole.ADMIN.name(), result.user().role());
    }

    @Test
    void login_throwsUnauthorizedException_whenEmailDoesNotExist() {
        var useCase = new LoginUserUseCase(email -> Optional.empty(), user -> "jwt-token", passwordEncoder);

        var exception = assertThrows(UnauthorizedException.class,
                () -> useCase.login(new LoginCommand("fabricio@email.com", "senhaSegura123")));

        assertEquals(INVALID_CREDENTIALS_MESSAGE, exception.getMessage());
    }

    @Test
    void login_throwsUnauthorizedException_whenPasswordDoesNotMatch() {
        var useCase = new LoginUserUseCase(email -> Optional.of(credentials(true)), user -> "jwt-token", passwordEncoder);

        var exception = assertThrows(UnauthorizedException.class,
                () -> useCase.login(new LoginCommand("fabricio@email.com", "senhaErrada123")));

        assertEquals(INVALID_CREDENTIALS_MESSAGE, exception.getMessage());
    }

    @Test
    void login_throwsForbiddenException_whenUserIsInactive() {
        var useCase = new LoginUserUseCase(email -> Optional.of(credentials(false)), user -> "jwt-token", passwordEncoder);

        var exception = assertThrows(ForbiddenException.class,
                () -> useCase.login(new LoginCommand("fabricio@email.com", "senhaSegura123")));

        assertEquals("Usuário inativo.", exception.getMessage());
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
