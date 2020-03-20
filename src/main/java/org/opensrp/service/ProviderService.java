package org.opensrp.service;

import org.opensrp.domain.Provider;
import org.opensrp.repository.couch.AllProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {
	
	private final AllProviders allProviders;
	
	@Autowired
	public ProviderService(AllProviders allProviders) {
		this.allProviders = allProviders;
	}
	
	public Provider getProviderByBaseEntityId(String baseEntityId) {
		return allProviders.findByBaseEntityId(baseEntityId);
	}
	
	public List<Provider> getAllProviders() {
		return allProviders.findAllProviders();
	}
	
	public void addProvider(Provider provider) {
		allProviders.add(provider);
	}
	
	public void updateProvider(Provider provider) {
		allProviders.update(provider);
	}
}
