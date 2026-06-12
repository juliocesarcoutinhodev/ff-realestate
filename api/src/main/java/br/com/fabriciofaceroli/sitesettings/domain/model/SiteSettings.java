package br.com.fabriciofaceroli.sitesettings.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record SiteSettings(
        UUID id,
        String brokerName,
        String brokerCreci,
        String brokerBio,
        String brokerPhotoUrl,
        String heroImageUrl,
        String heroTitle,
        String heroSubtitle,
        String phone,
        String whatsapp,
        String whatsappMessage,
        String email,
        String instagramUrl,
        String facebookUrl,
        String linkedinUrl,
        String metaDescription,
        LocalDateTime updatedAt
) {}
