package br.com.fabriciofaceroli.auth.domain.model;

import java.util.UUID;

public record UserCredentials(
        UUID id,
        String name,
        String email,
        String password,
        String role,
        boolean active
) {

    public User toUser() {
        return new User(id, name, email, role, active);
    }
}
