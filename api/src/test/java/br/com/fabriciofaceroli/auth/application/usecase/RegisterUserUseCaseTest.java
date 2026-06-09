package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.RegisterCommand;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserByEmailPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveUserPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Nota: validação de formato de senha (mínimo 8 chars, maiúscula, número) é responsabilidade
// de RegisterRequest via @Pattern (Jakarta Validation). O use case nunca recebe senha inválida.
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private FindUserByEmailPort findUserByEmailPort;

    @Mock
    private SaveUserPort saveUserPort;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(findUserByEmailPort, saveUserPort, passwordEncoder);
    }

    @Test
    void register_shouldReturnCreatedUser_whenEmailIsNotRegistered() {
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");
        var savedUser = new User(UUID.randomUUID(), command.name(), command.email(), UserRole.ADMIN.name(), true);

        when(findUserByEmailPort.findByEmail(command.email())).thenReturn(Optional.empty());
        when(saveUserPort.save(any(), anyString())).thenReturn(savedUser);

        var result = useCase.register(command);

        assertThat(result.name()).isEqualTo("Fabrício Faceroli");
        assertThat(result.email()).isEqualTo("fabricio@email.com");
        assertThat(result.role()).isEqualTo(UserRole.ADMIN.name());
        assertThat(result.active()).isTrue();
    }

    @Test
    void register_shouldEncodePassword_whenSavingNewUser() {
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");
        var savedUser = new User(UUID.randomUUID(), command.name(), command.email(), UserRole.ADMIN.name(), true);

        when(findUserByEmailPort.findByEmail(any())).thenReturn(Optional.empty());
        when(saveUserPort.save(any(), anyString())).thenReturn(savedUser);

        useCase.register(command);

        var passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(saveUserPort).save(any(), passwordCaptor.capture());
        assertThat(passwordCaptor.getValue()).isNotEqualTo("senhaSegura123");
        assertThat(passwordEncoder.matches("senhaSegura123", passwordCaptor.getValue())).isTrue();
    }

    @Test
    void register_shouldSaveUserWithAdminRoleAndActiveTrue_whenRegisteringNewUser() {
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");
        var savedUser = new User(UUID.randomUUID(), command.name(), command.email(), UserRole.ADMIN.name(), true);

        when(findUserByEmailPort.findByEmail(any())).thenReturn(Optional.empty());
        when(saveUserPort.save(any(), anyString())).thenReturn(savedUser);

        useCase.register(command);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(saveUserPort).save(userCaptor.capture(), any());
        assertThat(userCaptor.getValue().role()).isEqualTo(UserRole.ADMIN.name());
        assertThat(userCaptor.getValue().active()).isTrue();
    }

    @Test
    void register_shouldThrowConflictException_whenEmailAlreadyExists() {
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");
        var existingUser = new User(UUID.randomUUID(), "Outro", command.email(), UserRole.ADMIN.name(), true);

        when(findUserByEmailPort.findByEmail(command.email())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> useCase.register(command))
                .isInstanceOf(ConflictException.class)
                .hasMessage("E-mail já cadastrado.");
    }

    @Test
    void register_shouldNotCallSavePort_whenEmailAlreadyExists() {
        var command = new RegisterCommand("Fabrício Faceroli", "fabricio@email.com", "senhaSegura123");
        var existingUser = new User(UUID.randomUUID(), "Outro", command.email(), UserRole.ADMIN.name(), true);

        when(findUserByEmailPort.findByEmail(command.email())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> useCase.register(command)).isInstanceOf(ConflictException.class);
        verify(saveUserPort, never()).save(any(), any());
    }
}
