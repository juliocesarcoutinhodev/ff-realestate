package br.com.fabriciofaceroli.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(String domain, boolean secure) {}
