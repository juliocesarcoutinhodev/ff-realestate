package br.com.fabriciofaceroli.sitesettings.application.usecase;

import br.com.fabriciofaceroli.shared.exception.BusinessException;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UpdateSiteSettingsCommand;
import br.com.fabriciofaceroli.sitesettings.application.port.in.UpdateSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.SaveSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSiteSettingsUseCase implements UpdateSiteSettingsPort {

    private static final String WHATSAPP_PATTERN = "\\d{10,}";

    private final FindSiteSettingsPort findSiteSettingsPort;
    private final SaveSiteSettingsPort saveSiteSettingsPort;

    public UpdateSiteSettingsUseCase(FindSiteSettingsPort findSiteSettingsPort,
                                     SaveSiteSettingsPort saveSiteSettingsPort) {
        this.findSiteSettingsPort = findSiteSettingsPort;
        this.saveSiteSettingsPort = saveSiteSettingsPort;
    }

    @Override
    @Transactional
    @CacheEvict(value = "site-settings", allEntries = true)
    public SiteSettings update(UpdateSiteSettingsCommand command) {
        validate(command);
        var existing = findSiteSettingsPort.find();

        var updated = new SiteSettings(
                existing.id(),
                command.brokerName(),
                command.brokerCreci(),
                command.brokerBio(),
                command.brokerPhotoUrl(),
                command.heroImageUrl(),
                command.heroTitle(),
                command.heroSubtitle(),
                command.phone(),
                command.whatsapp(),
                command.whatsappMessage(),
                command.email(),
                command.instagramUrl(),
                command.facebookUrl(),
                command.linkedinUrl(),
                command.metaDescription(),
                null
        );

        return saveSiteSettingsPort.save(updated);
    }

    private void validate(UpdateSiteSettingsCommand command) {
        if (command.brokerName() == null || command.brokerName().isBlank()) {
            throw new BusinessException("Nome do corretor é obrigatório.");
        }
        if (command.brokerCreci() == null || command.brokerCreci().isBlank()) {
            throw new BusinessException("CRECI é obrigatório.");
        }
        if (command.whatsapp() == null || command.whatsapp().isBlank()) {
            throw new BusinessException("WhatsApp é obrigatório.");
        }
        if (!command.whatsapp().matches(WHATSAPP_PATTERN)) {
            throw new BusinessException("whatsapp deve conter apenas dígitos, com mínimo de 10 caracteres.");
        }
    }
}
