/**
 * Named interface exposing JWT and cookie services to domain module adapters.
 * Intentional cross-module access: auth.infrastructure.security adapters implement
 * auth output ports using these infrastructure services.
 */
@NamedInterface("security")
package br.com.fabriciofaceroli.infrastructure.security;

import org.springframework.modulith.NamedInterface;
