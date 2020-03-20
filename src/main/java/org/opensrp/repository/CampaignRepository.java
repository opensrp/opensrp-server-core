package org.opensrp.repository;

import org.opensrp.domain.Campaign;

import java.util.List;

public interface CampaignRepository  extends BaseRepository<Campaign>{
	
	public List<Campaign> getCampaignsByServerVersion(long serverVersion);
	public List<Campaign> getCampaignsByIdentifiers(String identifiers);

}
