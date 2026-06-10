package br.com.fabriciofaceroli.auth.application.usecase;

import br.com.fabriciofaceroli.auth.application.port.in.GetCurrentUserPort;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserByEmailPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetCurrentUserUseCase implements GetCurrentUserPort {

    private final FindUserByEmailPort findUserByEmailPort;

    public GetCurrentUserUseCase(FindUserByEmailPort findUserByEmailPort) {
        this.findUserByEmailPort = findUserByEmailPort;
    }

    @Override
    public User getMe(String email) {
        return findUserByEmailPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Sessão inválida."));
    }
}
