package org.opensrp.repository.postgres.mapper.custom;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.LocationDetail;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.LocationMetadataExample;
import org.opensrp.repository.postgres.mapper.LocationMetadataMapper;
import org.opensrp.search.LocationSearchBean;

public interface CustomLocationMetadataMapper extends LocationMetadataMapper {
	
	Location findById(@Param("id") String id, @Param("geometry") boolean returnGeometry);

	List<Location> selectMany(@Param("example") LocationMetadataExample locationMetadataExample, @Param("offset") int offset,
	        @Param("limit") int limit);
	
	List<Location> selectManyByProperties(@Param("example") LocationMetadataExample locationMetadataExample,
	        @Param("properties") Map<String, String> properties, @Param("geometry") boolean returnGeometry,
	        @Param("offset") int offset, @Param("limit") int limit);

	List<Location> selectManyWithOptionalGeometry(@Param("example") LocationMetadataExample locationMetadataExample,
              @Param("geometry") boolean returnGeometry,
              @Param("offset") int offset, @Param("limit") int limit);

	List<Location> selectWithChildren(@Param("example") LocationMetadataExample locationMetadataExample,
			@Param("geometry") boolean returnGeometry,
			@Param("locationIds") Set<String> locationIds,
			@Param("offset") int offset, @Param("limit") int limit);

	LinkedHashSet<LocationDetail> selectDetailsByPlanId(@Param("example") LocationMetadataExample locationMetadataExample,
											   @Param("planIdentifier") String planIdentifier);

	List<String> selectManyIds(@Param("example") LocationMetadataExample locationMetadataExample, @Param("offset") int offset,
							   @Param("limit") int limit);
	
	List<Location> selectLocations(@Param("locationSearchBean") LocationSearchBean locationSearchBean,
	                                     @Param("offset") Integer offset, @Param("limit") Integer limit);

	int selectCountLocations(@Param("locationSearchBean") LocationSearchBean locationSearchBean);
	
	LinkedHashSet<LocationDetail> selectLocationHierachy( @Param("identifiers") Set<String> identifiers, @Param("tags") boolean returnTags);

	Location findByIdAndVersion(@Param("id") String id, @Param("geometry") boolean returnGeometry, @Param("version") int version);

	LinkedHashSet<LocationDetail> selectLocationWithDescendants(@Param("locationId") String locationId, @Param("tags") boolean returnTags);

	List<String> selectChildrenIds(@Param("locationId") String locationId);
}
