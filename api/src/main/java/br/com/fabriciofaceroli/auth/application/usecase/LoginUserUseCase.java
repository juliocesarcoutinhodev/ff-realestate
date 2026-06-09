package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.LoginCommand;
import br.com.fabriciofaceroli.auth.application.port.in.LoginResult;
import br.com.fabriciofaceroli.auth.application.port.in.LoginUserPort;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserCredentialsByEmailPort;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateAuthTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.GenerateRefreshTokenPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveRefreshTokenPort;
import br.com.fabriciofaceroli.shared.exception.ForbiddenException;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginUserUseCase implements LoginUserPort {

    private static final String INVALID_CREDENTIALS_MESSAGE = "E-mail ou senha inválidos.";

    private final FindUserCredentialsByEmailPort findUserCredentialsByEmailPort;
    private final GenerateAuthTokenPort generateAuthTokenPort;
    private final GenerateRefreshTokenPort generateRefreshTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCase(FindUserCredentialsByEmailPort findUserCredentialsByEmailPort,
                            GenerateAuthTokenPort generateAuthTokenPort,
                            GenerateRefreshTokenPort generateRefreshTokenPort,
                            SaveRefreshTokenPort saveRefreshTokenPort,
                            PasswordEncoder passwordEncoder) {
        this.findUserCredentialsByEmailPort = findUserCredentialsByEmailPort;
        this.generateAuthTokenPort = generateAuthTokenPort;
        this.generateRefreshTokenPort = generateRefreshTokenPort;
        this.saveRefreshTokenPort = saveRefreshTokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        var credentials = findUserCredentialsByEmailPort.findCredentialsByEmail(command.email())
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(command.password(), credentials.password())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE);
        }

        if (!credentials.active()) {
            throw new ForbiddenException("Usuário inativo, contate o administrador.");
        }

        var user = credentials.toUser();
        var accessToken = generateAuthTokenPort.generateToken(user);
        var refreshTokenData = generateRefreshTokenPort.generateRefreshToken(user);
        saveRefreshTokenPort.save(user.id(), refreshTokenData);

        return new LoginResult(user, accessToken, refreshTokenData.tokenValue());
    }
}
