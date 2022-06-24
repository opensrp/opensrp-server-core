package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Campaign;
import org.opensrp.repository.postgres.mapper.CampaignMapper;

public interface CustomCampaignMapper extends CampaignMapper {

    int insertSelectiveAndSetId(Campaign campaign);

}
