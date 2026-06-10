package br.com.fabriciofaceroli.auth.domain.model;

import java.util.UUID;

public record User(UUID id, String name, String email, String role, boolean active) {}
