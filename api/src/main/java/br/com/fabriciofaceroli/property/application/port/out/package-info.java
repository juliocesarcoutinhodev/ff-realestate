/**
 * Named interface exposing property output ports to cross-module adapters.
 * Intentional cross-module access: photo.infrastructure.persistence.adapter.PhotoPropertyLookupAdapter
 * uses FindPropertyByIdPort from this package.
 */
@NamedInterface("ports-out")
package br.com.fabriciofaceroli.property.application.port.out;

import org.springframework.modulith.NamedInterface;
