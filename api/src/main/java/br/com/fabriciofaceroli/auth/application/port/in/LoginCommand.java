package br.com.fabriciofaceroli.auth.application.port.in;

public record LoginCommand(String email, String password) {}
