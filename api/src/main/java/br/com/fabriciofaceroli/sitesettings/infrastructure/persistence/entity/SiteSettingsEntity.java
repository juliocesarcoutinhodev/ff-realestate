package br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "site_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "broker_name", nullable = false, length = 100)
    private String brokerName;

    @Column(name = "broker_creci", nullable = false, length = 30)
    private String brokerCreci;

    @Column(name = "broker_bio", columnDefinition = "TEXT")
    private String brokerBio;

    @Column(name = "broker_photo_url", columnDefinition = "TEXT")
    private String brokerPhotoUrl;

    @Column(name = "hero_image_url", columnDefinition = "TEXT")
    private String heroImageUrl;

    @Column(name = "hero_title", length = 200)
    private String heroTitle;

    @Column(name = "hero_subtitle", length = 300)
    private String heroSubtitle;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 20)
    private String whatsapp;

    @Column(name = "whatsapp_message", length = 300)
    private String whatsappMessage;

    @Column(length = 150)
    private String email;

    @Column(name = "instagram_url", columnDefinition = "TEXT")
    private String instagramUrl;

    @Column(name = "facebook_url", columnDefinition = "TEXT")
    private String facebookUrl;

    @Column(name = "linkedin_url", columnDefinition = "TEXT")
    private String linkedinUrl;

    @Column(name = "meta_description", length = 300)
    private String metaDescription;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
