package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.RegisterCommand;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserByEmailPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveUserPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterUserUseCaseTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void register_createsAdminUser_withEncodedPassword() {
        var savePort = new CapturingSaveUserPort();
        var useCase = new RegisterUserUseCase(email -> Optional.empty(), savePort, passwordEncoder);
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");

        var user = useCase.register(command);

        assertEquals("Fabrício Faceroli", user.name());
        assertEquals("fabricio@email.com", user.email());
        assertEquals(UserRole.ADMIN.name(), user.role());
        assertTrue(user.active());
        assertNotEquals("senhaSegura123", savePort.encodedPassword);
        assertTrue(passwordEncoder.matches("senhaSegura123", savePort.encodedPassword));
    }

    @Test
    void register_throwsConflictException_whenEmailAlreadyExists() {
        FindUserByEmailPort findUserByEmailPort = email -> Optional.of(
                new User(UUID.randomUUID(), "Fabrício Faceroli", email, UserRole.ADMIN.name(), true)
        );
        var useCase = new RegisterUserUseCase(findUserByEmailPort, new CapturingSaveUserPort(), passwordEncoder);
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");

        var exception = assertThrows(ConflictException.class, () -> useCase.register(command));

        assertEquals("E-mail já cadastrado.", exception.getMessage());
    }

    private static class CapturingSaveUserPort implements SaveUserPort {

        private String encodedPassword;

        @Override
        public User save(User user, String encodedPassword) {
            this.encodedPassword = encodedPassword;
            return new User(UUID.randomUUID(), user.name(), user.email(), user.role(), user.active());
        }
    }
}
