package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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
		if (getCampaign(campaign.getIdentifier()) != null) {
			updateCampaign(campaign);
		} else {
			addCampaign(campaign);
		}
	}

	public Campaign addCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaignRepository.add(campaign);
		return campaign;
	}

	public Campaign updateCampaign(Campaign campaign) {
		if (StringUtils.isBlank(campaign.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		campaignRepository.update(campaign);
		return campaign;
	}

	public Campaign getCampaign(String identifier) {
		if (StringUtils.isBlank(identifier))
			return null;
		return campaignRepository.get(identifier);
	}
	public List<Campaign> getCampaignsByIdentifiers(String identifiers) {
		if (StringUtils.isBlank(identifiers))
			return null;
		return campaignRepository.getCampaignsByIdentifiers(identifiers);
	}

	public List<Campaign> getCampaignsByServerVersion(long serverVersion) {
		return campaignRepository.getCampaignsByServerVersion(serverVersion);
	}

}
