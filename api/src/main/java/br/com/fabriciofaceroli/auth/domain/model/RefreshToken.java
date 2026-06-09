package br.com.fabriciofaceroli.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(UUID id, UUID userId, String token, Instant expiresAt, boolean revoked) {}
