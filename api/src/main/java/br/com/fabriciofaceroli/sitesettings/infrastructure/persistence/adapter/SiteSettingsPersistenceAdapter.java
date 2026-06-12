package br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.adapter;

import br.com.fabriciofaceroli.shared.exception.ResourceNotFoundException;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.SaveSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.mapper.SiteSettingsMapper;
import br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.repository.SiteSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SiteSettingsPersistenceAdapter implements FindSiteSettingsPort, SaveSiteSettingsPort {

    private final SiteSettingsRepository repository;
    private final SiteSettingsMapper mapper;

    @Override
    public SiteSettings find() {
        return repository.findFirstBy()
                .map(mapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Configurações do site não encontradas."));
    }

    @Override
    public SiteSettings save(SiteSettings settings) {
        var entity = mapper.toEntity(settings);
        return mapper.toDomain(repository.save(entity));
    }
}
