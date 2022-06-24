package org.opensrp.repository;

import org.opensrp.domain.viewconfiguration.ViewConfiguration;

import java.util.List;

public interface ViewConfigurationRepository extends BaseRepository<ViewConfiguration> {

    List<ViewConfiguration> findAllViewConfigurations();

    List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion);

    List<ViewConfiguration> findByEmptyServerVersion();
}
