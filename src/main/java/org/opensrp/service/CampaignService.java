package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Campaign;
import org.opensrp.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {

	private CampaignRepository campaignRepository;

	@Autowired
	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	public List<Campaign> getAllCampaigns() {
		return campaignRepository.getAll();
	}

	public void addOrUpdateCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaign.setServerVersion(System.currentTimeMillis());
		if (campaignRepository.get(campaign.getIdentifier()) != null) {
			campaignRepository.update(campaign);
		} else {
			campaign.setAuthoredOn(new DateTime());
			campaignRepository.add(campaign);
		}
	}

	public Campaign addCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaign.setServerVersion(System.currentTimeMillis());
		campaignRepository.add(campaign);
		return campaign;
	}

	public Campaign updateCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaign.setServerVersion(System.currentTimeMillis());
		campaignRepository.update(campaign);
		return campaign;
	}

	public Campaign getCampaign(String identifier) {
		if (StringUtils.isBlank(identifier))
			return null;
		return campaignRepository.get(identifier);
	}
	public List<Campaign> getCampaignsByIdentifiers(String identifier) {
		if (StringUtils.isBlank(identifier))
			return null;
		return campaignRepository.getCampaignsByIdentifiers(identifier);
	}

	public List<Campaign> getCampaignsByServerVersion(long serverVersion) {
		return campaignRepository.getCampaignsByServerVersion(serverVersion);
	}

}
