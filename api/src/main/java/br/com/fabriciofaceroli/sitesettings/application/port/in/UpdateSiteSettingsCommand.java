package br.com.fabriciofaceroli.sitesettings.application.port.in;

public record UpdateSiteSettingsCommand(
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
        String metaDescription
) {}
