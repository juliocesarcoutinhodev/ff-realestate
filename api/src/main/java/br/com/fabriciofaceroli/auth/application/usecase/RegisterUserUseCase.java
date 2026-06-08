package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.RegisterCommand;
import br.com.fabriciofaceroli.auth.application.port.in.RegisterUserPort;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserByEmailPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveUserPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.shared.exception.ConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserUseCase implements RegisterUserPort {

    private final FindUserByEmailPort findUserByEmailPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(FindUserByEmailPort findUserByEmailPort,
                               SaveUserPort saveUserPort,
                               PasswordEncoder passwordEncoder) {
        this.findUserByEmailPort = findUserByEmailPort;
        this.saveUserPort = saveUserPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterCommand command) {
        if (findUserByEmailPort.findByEmail(command.email()).isPresent()) {
            throw new ConflictException("E-mail já cadastrado.");
        }
        var encodedPassword = passwordEncoder.encode(command.password());
        var user = new User(null, command.name(), command.email(), UserRole.ADMIN.name(), true);
        return saveUserPort.save(user, encodedPassword);
    }
}
