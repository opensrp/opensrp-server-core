package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.opensrp.domain.LocationProperty;
import org.opensrp.domain.postgres.Structure;
import org.opensrp.repository.postgres.mapper.StructureMapper;

public interface CustomStructureMapper extends StructureMapper {
	int insertSelectiveAndSetId(Structure structure);

	List<LocationProperty> selectWithinRadius(double latitude, double longitude, double radius);
}
