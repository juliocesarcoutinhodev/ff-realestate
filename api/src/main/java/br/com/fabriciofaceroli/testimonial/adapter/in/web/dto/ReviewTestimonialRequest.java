package br.com.fabriciofaceroli.testimonial.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ReviewTestimonialRequest(

        @NotBlank(message = "Status é obrigatório")
        @Pattern(regexp = "APPROVED|REJECTED", message = "Status deve ser APPROVED ou REJECTED")
        @Schema(description = "Novo status do depoimento", example = "APPROVED", allowableValues = {"APPROVED", "REJECTED"})
        String status
) {}
