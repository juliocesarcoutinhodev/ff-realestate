package br.com.fabriciofaceroli.sitesettings.application.usecase;

import br.com.fabriciofaceroli.sitesettings.application.port.in.GetSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.application.port.out.FindSiteSettingsPort;
import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetSiteSettingsUseCase implements GetSiteSettingsPort {

    private final FindSiteSettingsPort findSiteSettingsPort;

    public GetSiteSettingsUseCase(FindSiteSettingsPort findSiteSettingsPort) {
        this.findSiteSettingsPort = findSiteSettingsPort;
    }

    @Override
    @Cacheable(value = "site-settings", key = "'settings'")
    public SiteSettings get() {
        return findSiteSettingsPort.find();
    }
}
