package org.opensrp.repository;

import org.opensrp.domain.PlanDefinition;

import java.util.List;

/**
 * Created by Vincent Karuri on 06/05/2019
 */
public interface PlanRepository extends BaseRepository<PlanDefinition> {

    List<PlanDefinition> getPlansByServerVersionAndOperationalAreas(Long serverVersion, List<String> operationalAreaIds);

    List<PlanDefinition> getPlansByIdsReturnOptionalFields(List<String> ids, List<String> fields);
}
