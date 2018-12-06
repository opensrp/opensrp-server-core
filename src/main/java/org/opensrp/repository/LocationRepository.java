package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.PhysicalLocation;
import org.opensrp.search.EventSearchBean;
import org.opensrp.search.LocationSearchBean;

public interface LocationRepository extends BaseRepository<PhysicalLocation> {

	PhysicalLocation getStructure(String id);

	List<PhysicalLocation> findLocationsByServerVersion(long serverVersion);

	List<PhysicalLocation> findLocationsByNames(LocationSearchBean locationSearchBean);

	List<PhysicalLocation> findStructuresByParentAndServerVersion(String parentId, long serverVersion);

	List<PhysicalLocation> findByEmptyServerVersion();

	List<PhysicalLocation> findStructuresByEmptyServerVersion();

	List<PhysicalLocation> getAllStructures();
}
