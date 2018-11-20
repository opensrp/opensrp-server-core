package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Structure;
import org.opensrp.repository.postgres.mapper.StructureMapper;

public interface CustomStructureMapper extends StructureMapper {
	int insertSelectiveAndSetId(Structure structure);
}
