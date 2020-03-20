package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Campaign;
import org.opensrp.domain.postgres.CampaignMetadataExample;
import org.opensrp.repository.postgres.mapper.CampaignMetadataMapper;

import java.util.List;

public interface CustomCampaignMetadataMapper extends CampaignMetadataMapper {

	Campaign selectByIdentifier(String identifier);

	List<Campaign> selectMany(@Param("example") CampaignMetadataExample campaignMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit);

}
