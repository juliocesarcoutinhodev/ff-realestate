package br.com.fabriciofaceroli.auth.application.port.out;

import java.time.Instant;

public record RefreshTokenData(String tokenValue, Instant expiresAt) {}
