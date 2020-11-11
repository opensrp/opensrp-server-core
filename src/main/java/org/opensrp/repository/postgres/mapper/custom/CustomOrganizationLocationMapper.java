/**
 * 
 */
package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.postgres.OrganizationLocation;
import org.opensrp.domain.postgres.OrganizationLocationExample.Criteria;
import org.opensrp.repository.postgres.mapper.OrganizationLocationMapper;
import org.opensrp.search.AssignedLocationAndPlanSearchBean;

/**
 * @author Samuel Githengi created on 09/09/19
 */
public interface CustomOrganizationLocationMapper extends OrganizationLocationMapper {

	/**
	 * Gets the assigned locations and plans for an organization
	 * 
	 * @param organizationId
	 * @return
	 */
	List<AssignedLocations> findAssignedlocationsAndPlans(@Param("searchBean") AssignedLocationAndPlanSearchBean searchBean,
			@Param("offset") int offset, @Param("limit") int limit, @Param("oredCriteria") List<Criteria> oredCriteria,
			@Param("toDate") Date toDate);

	/**
	 * Gets the locations and plans assigned to an organization valid until now
	 * 
	 * @param example       the criteria for filtering
	 * @param orderByClause the order by clause
	 * @param currentDate   todays date
	 * @return organization locations and plans assigned to an organization valid
	 *         until now
	 */
	List<OrganizationLocation> selectByExampleAndDateTo(@Param("oredCriteria") List<Criteria> oredCriteria,
			@Param("orderByClause") String orderByClause, @Param("currentDate") Date currentDate);

}
