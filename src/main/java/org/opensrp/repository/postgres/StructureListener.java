package org.opensrp.repository.postgres;

import org.opensrp.domain.postgres.Structure;

public interface StructureListener {
	void onCreateOrUpdateStructure(Structure structure);
}
