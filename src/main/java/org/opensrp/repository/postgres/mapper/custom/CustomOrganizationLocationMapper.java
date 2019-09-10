/**
 * 
 */
package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.repository.postgres.mapper.OrganizationLocationMapper;

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
	List<AssignedLocations> findAssignedlocationsAndPlans(@Param("organizationId") Long organizationId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

}
