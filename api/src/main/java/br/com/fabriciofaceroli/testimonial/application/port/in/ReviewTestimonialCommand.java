package br.com.fabriciofaceroli.testimonial.application.port.in;

import java.util.UUID;

public record ReviewTestimonialCommand(UUID id, String newStatus) {}
