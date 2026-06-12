package br.com.fabriciofaceroli.sitesettings.application.port.out;

import br.com.fabriciofaceroli.sitesettings.domain.model.SiteSettings;

public interface FindSiteSettingsPort {
    SiteSettings find();
}
