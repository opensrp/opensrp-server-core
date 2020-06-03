package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Campaign;
import org.opensrp.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

	private CampaignRepository campaignRepository;

	@Autowired
	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	@PreAuthorize("hasRole('CAMPAIGN_VIEW')")
	public List<Campaign> getAllCampaigns() {
		return campaignRepository.getAll();
	}

	
	public void addOrUpdateCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaign.setServerVersion(System.currentTimeMillis());
		if (getCampaign(campaign.getIdentifier()) != null) {
			updateCampaign(campaign);
		} else {
			campaign.setAuthoredOn(new DateTime());
			addCampaign(campaign);
		}
	}
	
	@PreAuthorize("hasRole('CAMPAIGN_CREATE')")
	public Campaign addCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaign.setServerVersion(System.currentTimeMillis());
		campaignRepository.add(campaign);
		return campaign;
	}

	@PreAuthorize("hasRole('CAMPAIGN_UPDATE')")
	public Campaign updateCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaign.setServerVersion(System.currentTimeMillis());
		campaignRepository.update(campaign);
		return campaign;
	}

	@PreAuthorize("hasRole('CAMPAIGN_VIEW')")
	public Campaign getCampaign(String identifier) {
		if (StringUtils.isBlank(identifier))
			return null;
		return campaignRepository.get(identifier);
	}
	
	@PreAuthorize("hasRole('CAMPAIGN_VIEW')")
	public List<Campaign> getCampaignsByIdentifiers(String identifiers) {
		if (StringUtils.isBlank(identifiers))
			return null;
		return campaignRepository.getCampaignsByIdentifiers(identifiers);
	}

	@PreAuthorize("hasRole('CAMPAIGN_SYNC')")
	public List<Campaign> getCampaignsByServerVersion(long serverVersion) {
		return campaignRepository.getCampaignsByServerVersion(serverVersion);
	}

}
