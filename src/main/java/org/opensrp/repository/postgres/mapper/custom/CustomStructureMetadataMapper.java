package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.LocationAndStock;
import org.opensrp.domain.StructureCount;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.Structure;
import org.opensrp.domain.postgres.StructureMetadataExample;
import org.opensrp.repository.postgres.mapper.StructureMetadataMapper;

public interface CustomStructureMetadataMapper extends StructureMetadataMapper {

	Structure findById(@Param("id") String id, @Param("geometry") boolean returnGeometry);

	List<Structure> selectMany(@Param("example") StructureMetadataExample locationMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit);

	List<Location> selectManyByProperties(@Param("example") StructureMetadataExample locationMetadataExample,
			@Param("properties") Map<String, String> properties, @Param("geometry") boolean returnGeometry,
			@Param("offset") int offset, @Param("limit") int limit);

	List<String> selectManyIds(@Param("example") StructureMetadataExample structureMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit);

	List<StructureCount> findStructureCountsForLocation(@Param("locationIdentifiers") Set<String> locationIdentifiers);

	Long countMany(@Param("example") StructureMetadataExample structureMetadataExample);

	List<LocationAndStock> findStructureAndStocksByJurisdiction(@Param("example") StructureMetadataExample locationMetadataExample,
		@Param("properties") Map<String, String> properties, @Param("geometry") boolean returnGeometry,
		@Param("offset") int offset, @Param("limit") int limit);

}
