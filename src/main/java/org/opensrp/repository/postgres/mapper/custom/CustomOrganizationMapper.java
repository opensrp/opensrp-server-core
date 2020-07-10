/**
 * 
 */
package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Organization;
import org.opensrp.domain.postgres.OrganizationExample;
import org.opensrp.repository.postgres.mapper.OrganizationMapper;
import org.opensrp.search.OrganizationSearchBean;

/**
 * @author Samuel Githengi created on 08/30/19
 */
public interface CustomOrganizationMapper extends OrganizationMapper {

	/**
	 * Return list of Organization filtered by example with offset and limit
	 * 
	 * @param example used to filter organizations
	 * @param offset
	 * @param limit
	 * @return the list of organizations
	 */
	List<Organization> selectMany(@Param("example") OrganizationExample example, @Param("offset") int offset,
			@Param("limit") int limit);
	
	List<org.opensrp.domain.Organization> selectSearchOrganizations(@Param("searchBean") OrganizationSearchBean searchBean,
	                                                                @Param("offset") int offset, @Param("limit") int limit);


	int selectOrganizationCount(@Param("searchBean") OrganizationSearchBean searchBean);

	List<Organization> selectOrganizationsEncompassLocations(@Param("identifier") String identifier, @Param("activeDate") Date activeDate);
}
