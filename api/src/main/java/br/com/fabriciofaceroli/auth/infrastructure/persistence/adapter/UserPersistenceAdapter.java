package br.com.fabriciofaceroli.auth.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.auth.application.port.out.FindUserByEmailPort;
import br.com.fabriciofaceroli.auth.application.port.out.FindUserCredentialsByEmailPort;
import br.com.fabriciofaceroli.auth.application.port.out.SaveUserPort;
import br.com.fabriciofaceroli.auth.domain.model.User;
import br.com.fabriciofaceroli.auth.domain.model.UserCredentials;
import br.com.fabriciofaceroli.auth.domain.model.UserRole;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.entity.UserEntity;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.mapper.UserMapper;
import br.com.fabriciofaceroli.auth.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class UserPersistenceAdapter implements FindUserByEmailPort, FindUserCredentialsByEmailPort, SaveUserPort {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserPersistenceAdapter(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUser);
    }

    @Override
    public Optional<UserCredentials> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserCredentials);
    }

    @Override
    public User save(User user, String encodedPassword) {
        var now = LocalDateTime.now();
        var entity = UserEntity.builder()
                .name(user.name())
                .email(user.email())
                .password(encodedPassword)
                .role(UserRole.valueOf(user.role()))
                .active(user.active())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return userMapper.toUser(userRepository.save(entity));
    }
}
