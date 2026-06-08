/**
 * Cross-cutting module — open to all other modules by design.
 * Contains shared utilities, exceptions, response types, security, and configuration.
 */
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package br.com.fabriciofaceroli.shared;

import org.springframework.modulith.ApplicationModule;
