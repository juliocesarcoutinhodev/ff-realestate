/**
 * Named interface exposing the Property domain record to cross-module adapters.
 * Intentional cross-module access: testimonial module reads id/title/slug to build PropertySummary.
 */
@NamedInterface("domain")
package br.com.fabriciofaceroli.property.domain.model;

import org.springframework.modulith.NamedInterface;
