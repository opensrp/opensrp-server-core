package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.repository.ViewConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ViewConfigurationService {
	
	private ViewConfigurationRepository viewConfigurationRepository;
	
	@Autowired
	public void setViewConfigurationRepository(ViewConfigurationRepository viewConfigurationRepository) {
		this.viewConfigurationRepository = viewConfigurationRepository;
	}

	@PreAuthorize("hasRole('SYNC_VIEWCONFIGURATION')")
	public List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion) {
		return viewConfigurationRepository.findViewConfigurationsByVersion(lastSyncedServerVersion);
	}
	
}
