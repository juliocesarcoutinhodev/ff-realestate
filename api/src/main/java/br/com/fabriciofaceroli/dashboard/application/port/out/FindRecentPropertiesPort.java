package br.com.fabriciofaceroli.dashboard.application.port.out;

import br.com.fabriciofaceroli.dashboard.domain.model.RecentProperty;

import java.util.List;

public interface FindRecentPropertiesPort {
    List<RecentProperty> findRecent(int limit);
}
