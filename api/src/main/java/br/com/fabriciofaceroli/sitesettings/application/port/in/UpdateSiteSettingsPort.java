package br.com.fabriciofaceroli.sitesettings.application.port.in;

import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;

public interface UpdateSiteSettingsPort {
    SiteSettings update(UpdateSiteSettingsCommand command);
}
