package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.LocationMetadataExample;
import org.opensrp.repository.postgres.mapper.LocationMetadataMapper;

public interface CustomLocationMetadataMapper extends LocationMetadataMapper {
	
	Location findById(String id);
	
	List<Location> selectMany(@Param("example") LocationMetadataExample locationMetadataExample, @Param("offset") int offset,
	        @Param("limit") int limit);
	
	List<Location> selectManyByProperties(@Param("example") LocationMetadataExample locationMetadataExample,
	        @Param("properties") Map<String, String> properties, @Param("geometry") boolean returnGeometry,
	        @Param("offset") int offset, @Param("limit") int limit);

	List<Location> selectManyById(@Param("example") LocationMetadataExample locationMetadataExample,
			@Param("geometry") boolean returnGeometry,
			@Param("offset") int offset, @Param("limit") int limit);
	
}
