package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Campaign;

public interface CampaignRepository  extends BaseRepository<Campaign>{
	
	public List<Campaign> getCampaignsByServerVersion(long serverVersion);
	public List<Campaign> getCampaignsByIdentifiers(String identifier);

}
