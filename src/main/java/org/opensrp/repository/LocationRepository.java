package org.opensrp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.StructureDetails;

public interface LocationRepository extends BaseRepository<PhysicalLocation> {

	PhysicalLocation getStructure(String id);

	List<PhysicalLocation> findLocationsByServerVersion(long serverVersion);

	List<PhysicalLocation> findLocationsByNames(String locationNames, long serverVersion);

	List<PhysicalLocation> findStructuresByParentAndServerVersion(String parentId, long serverVersion);

	List<PhysicalLocation> findByEmptyServerVersion();

	List<PhysicalLocation> findStructuresByEmptyServerVersion();

	List<PhysicalLocation> getAllStructures();

	Collection<StructureDetails> findStructureAndFamilyDetails(double latitude, double longitude, double radius);

	List<PhysicalLocation> findLocationsByProperties(boolean returnGeometry, String parentId,
			Map<String, String> properties);

	List<PhysicalLocation> findStructuresByProperties(boolean returnGeometry, String parentId,
			Map<String, String> properties);
}
