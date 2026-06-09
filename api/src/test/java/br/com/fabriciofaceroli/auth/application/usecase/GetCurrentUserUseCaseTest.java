package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserUseCaseTest {

    @Test
    void getMe_returnsUser_whenEmailExistsInDatabase() {
        var expected = user();
        var sut = new GetCurrentUserUseCase(email -> Optional.of(expected));

        var result = sut.getMe("fabricio@email.com");

        assertThat(result.email()).isEqualTo("fabricio@email.com");
        assertThat(result.name()).isEqualTo("Fabrício Faceroli");
        assertThat(result.role()).isEqualTo(UserRole.ADMIN.name());
    }

    @Test
    void getMe_throwsUnauthorizedException_whenUserNotFound() {
        var sut = new GetCurrentUserUseCase(email -> Optional.empty());

        assertThatThrownBy(() -> sut.getMe("naoexiste@email.com"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Sessão inválida.");
    }

    private User user() {
        return new User(UUID.randomUUID(), "Fabrício Faceroli", "fabricio@email.com", UserRole.ADMIN.name(), true);
    }
}
