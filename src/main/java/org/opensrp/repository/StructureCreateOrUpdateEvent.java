package org.opensrp.repository;

import org.opensrp.domain.postgres.Structure;
import org.springframework.context.ApplicationEvent;

public class StructureCreateOrUpdateEvent extends ApplicationEvent {
	public StructureCreateOrUpdateEvent(Structure source) {
		super(source);
	}
}


