package br.com.fabriciofaceroli.sitesettings.application.usecase;

import br.com.fabriciofaceroli.shared.exception.BusinessException;
import br.com.fabriciofaceroli.shared.exception.UnsupportedMediaTypeException;
import br.com.fabriciofaceroli.sitesettings.application.port.in.SettingsImageFile;
import br.com.fabriciofaceroli.sitesettings.application.port.in.SettingsImageType;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UploadSettingsImageCommand;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UploadSettingsImagePort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.DeleteSettingsFilePort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.SaveSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.UploadSettingsFilePort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UploadSettingsImageUseCase implements UploadSettingsImagePort {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp");

    private final FindSiteSettingsPort findSiteSettingsPort;
    private final SaveSiteSettingsPort saveSiteSettingsPort;
    private final UploadSettingsFilePort uploadSettingsFilePort;
    private final DeleteSettingsFilePort deleteSettingsFilePort;

    public UploadSettingsImageUseCase(FindSiteSettingsPort findSiteSettingsPort,
                                      SaveSiteSettingsPort saveSiteSettingsPort,
                                      UploadSettingsFilePort uploadSettingsFilePort,
                                      DeleteSettingsFilePort deleteSettingsFilePort) {
        this.findSiteSettingsPort = findSiteSettingsPort;
        this.saveSiteSettingsPort = saveSiteSettingsPort;
        this.uploadSettingsFilePort = uploadSettingsFilePort;
        this.deleteSettingsFilePort = deleteSettingsFilePort;
    }

    @Override
    @Transactional
    @CacheEvict(value = "site-settings", allEntries = true)
    public String upload(UploadSettingsImageCommand command) {
        validate(command.file());

        var existing = findSiteSettingsPort.find();
        var ext = extractExtension(command.file().originalFileName());
        var objectName = resolveObjectName(command.type(), ext);
        var oldUrl = resolveCurrentUrl(command.type(), existing);

        if (oldUrl != null && !oldUrl.isBlank()) {
            deleteSettingsFilePort.delete(oldUrl);
        }

        var newUrl = uploadSettingsFilePort.upload(objectName, command.file().contentType(), command.file().inputStream());

        var brokerPhotoUrl = command.type() == SettingsImageType.BROKER_PHOTO ? newUrl : existing.brokerPhotoUrl();
        var heroImageUrl = command.type() == SettingsImageType.HERO_IMAGE ? newUrl : existing.heroImageUrl();

        var updated = new SiteSettings(
                existing.id(),
                existing.brokerName(),
                existing.brokerCreci(),
                existing.brokerBio(),
                brokerPhotoUrl,
                heroImageUrl,
                existing.heroTitle(),
                existing.heroSubtitle(),
                existing.phone(),
                existing.whatsapp(),
                existing.whatsappMessage(),
                existing.email(),
                existing.instagramUrl(),
                existing.facebookUrl(),
                existing.linkedinUrl(),
                existing.metaDescription(),
                null
        );

        saveSiteSettingsPort.save(updated);
        return newUrl;
    }

    private void validate(SettingsImageFile file) {
        if (file.size() > MAX_SIZE_BYTES) {
            throw new BusinessException("Arquivo muito grande. Tamanho máximo permitido: 5MB.");
        }
        if (!isAllowedExtension(file.originalFileName()) && !isAllowedContentType(file.contentType())) {
            throw new UnsupportedMediaTypeException(
                    "Formato de arquivo não suportado. Formatos aceitos: jpg, jpeg, png, webp.");
        }
    }

    private String resolveObjectName(SettingsImageType type, String ext) {
        return switch (type) {
            case BROKER_PHOTO -> "settings/broker-photo." + ext;
            case HERO_IMAGE -> "settings/hero-image." + ext;
        };
    }

    private String resolveCurrentUrl(SettingsImageType type, SiteSettings settings) {
        return switch (type) {
            case BROKER_PHOTO -> settings.brokerPhotoUrl();
            case HERO_IMAGE -> settings.heroImageUrl();
        };
    }

    private boolean isAllowedExtension(String fileName) {
        if (fileName == null) return false;
        var dot = fileName.lastIndexOf('.');
        if (dot < 0) return false;
        return ALLOWED_EXTENSIONS.contains(fileName.substring(dot + 1).toLowerCase());
    }

    private boolean isAllowedContentType(String contentType) {
        if (contentType == null) return false;
        return ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    private String extractExtension(String fileName) {
        if (fileName == null) return "jpg";
        var dot = fileName.lastIndexOf('.');
        return dot < 0 ? "jpg" : fileName.substring(dot + 1).toLowerCase();
    }
}
