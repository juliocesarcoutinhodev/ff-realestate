package br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.repository;

import br.com.fabriciofaceroli.sitesettings.infrastructure.persistence.entity.SiteSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SiteSettingsRepository extends JpaRepository<SiteSettingsEntity, UUID> {
    Optional<SiteSettingsEntity> findFirstBy();
}
