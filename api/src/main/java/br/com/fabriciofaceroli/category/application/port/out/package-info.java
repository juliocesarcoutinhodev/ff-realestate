/**
 * Named interface exposing category output ports to cross-module adapters.
 * Intentional cross-module access: property.infrastructure.persistence.adapter.PropertyCountAdapter
 * implements CountPropertiesByCategoryPort from this package.
 */
@NamedInterface("ports-out")
package br.com.fabriciofaceroli.category.application.port.out;

import org.springframework.modulith.NamedInterface;
