package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Campaign;
import org.opensrp.domain.postgres.CampaignMetadata;
import org.opensrp.domain.postgres.CampaignMetadataExample;
import org.opensrp.repository.CampaignRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomCampaignMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomCampaignMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CampaignRepositoryImpl extends BaseRepositoryImpl<Campaign> implements CampaignRepository {

	@Autowired
	private CustomCampaignMapper campaignMapper;

	@Autowired
	private CustomCampaignMetadataMapper campaignMetadataMapper;

	@Override
	public Campaign get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}

		org.opensrp.domain.postgres.Campaign pgCampaign = campaignMetadataMapper.selectByIdentifier(id);
		if (pgCampaign == null) {
			return null;
		}
		return convert(pgCampaign);
	}

	@Override
	public void add(Campaign entity) {
		if (getUniqueField(entity) == null) {
			return;
		}

		if (retrievePrimaryKey(entity) != null) { // Campaign already added
			return;
		}

		org.opensrp.domain.postgres.Campaign pgCampaign = convert(entity, null);
		if (pgCampaign == null) {
			return;
		}
		
		int rowsAffected = campaignMapper.insertSelectiveAndSetId(pgCampaign);
		if (rowsAffected < 1 || pgCampaign.getId() == null) {
			return;
		}
		
		CampaignMetadata campaignMetadata = createMetadata(entity, pgCampaign.getId());
		if (campaignMetadata == null) {
			return;
		}
		
		campaignMetadataMapper.insertSelective(campaignMetadata);

	}

	@Override
	public void update(Campaign entity) {
		if (getUniqueField(entity) == null) {
			return;
		}

		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Campaign does not exist
			return;
		}

		org.opensrp.domain.postgres.Campaign pgCampaign = convert(entity, id);
		if (pgCampaign == null) {
			return;
		}
		CampaignMetadata campaignMetadata = createMetadata(entity, pgCampaign.getId());
		if (campaignMetadata == null) {
			return;
		}

		int rowsAffected = campaignMapper.updateByPrimaryKey(pgCampaign);
		if (rowsAffected < 1) {
			return;
		}

		CampaignMetadataExample campaignMetadataExample = new CampaignMetadataExample();
		campaignMetadataExample.createCriteria().andCampaignIdEqualTo(id);
		campaignMetadata.setId(campaignMetadataMapper.selectByExample(campaignMetadataExample).get(0).getId());
		campaignMetadataMapper.updateByPrimaryKey(campaignMetadata);

	}

	@Override
	public List<Campaign> getAll() {
		List<org.opensrp.domain.postgres.Campaign> campaigns = campaignMetadataMapper
				.selectMany(new CampaignMetadataExample(), 0, DEFAULT_FETCH_SIZE);
		return convert(campaigns);
	}

	@Override
	public List<Campaign> getCampaignsByServerVersion(long serverVersion) {
		CampaignMetadataExample campaignMetadataExample = new CampaignMetadataExample();
		campaignMetadataExample.createCriteria().andServerVersionGreaterThan(serverVersion);
		List<org.opensrp.domain.postgres.Campaign> campaigns = campaignMetadataMapper
				.selectMany(campaignMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(campaigns);
	}

	@Override
	public void safeRemove(Campaign entity) {
		if (entity == null) {
			return;
		}

		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}

		CampaignMetadataExample campaignMetadataExample = new CampaignMetadataExample();
		campaignMetadataExample.createCriteria().andCampaignIdEqualTo(id);
		int rowsAffected = campaignMetadataMapper.deleteByExample(campaignMetadataExample);
		if (rowsAffected < 1) {
			return;
		}

		campaignMapper.deleteByPrimaryKey(id);

	}

	@Override
	protected Long retrievePrimaryKey(Campaign campaign) {
		Object uniqueId = getUniqueField(campaign);
		if (uniqueId == null) {
			return null;
		}

		String identifier = uniqueId.toString();

		org.opensrp.domain.postgres.Campaign pgCampaign = campaignMetadataMapper.selectByIdentifier(identifier);
		if (pgCampaign == null) {
			return null;
		}
		return pgCampaign.getId();
	}

	@Override
	protected Object getUniqueField(Campaign campaign) {
		if (campaign == null) {
			return null;
		}
		return campaign.getIdentifier();
	}

	private Campaign convert(org.opensrp.domain.postgres.Campaign pgCampaign) {
		if (pgCampaign == null || pgCampaign.getJson() == null || !(pgCampaign.getJson() instanceof Campaign)) {
			return null;
		}
		return (Campaign) pgCampaign.getJson();
	}

	private org.opensrp.domain.postgres.Campaign convert(Campaign campaign, Long primaryKey) {
		if (campaign == null) {
			return null;
		}

		org.opensrp.domain.postgres.Campaign pgCampaign = new org.opensrp.domain.postgres.Campaign();
		pgCampaign.setId(primaryKey);
		pgCampaign.setJson(campaign);

		return pgCampaign;
	}

	private List<Campaign> convert(List<org.opensrp.domain.postgres.Campaign> campaigns) {
		if (campaigns == null || campaigns.isEmpty()) {
			return new ArrayList<>();
		}

		List<Campaign> convertedCampaigns = new ArrayList<>();
		for (org.opensrp.domain.postgres.Campaign campaign : campaigns) {
			Campaign convertedCampaign = convert(campaign);
			if (convertedCampaign != null) {
				convertedCampaigns.add(convertedCampaign);
			}
		}

		return convertedCampaigns;
	}

	private CampaignMetadata createMetadata(Campaign entity, Long id) {
		CampaignMetadata campaignMetadata = new CampaignMetadata();
		campaignMetadata.setCampaignId(id);
		campaignMetadata.setIdentifier(entity.getIdentifier());
		campaignMetadata.setServerVersion(entity.getServerVersion());
		return campaignMetadata;
	}

}
