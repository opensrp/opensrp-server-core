package org.opensrp.repository;

import org.opensrp.domain.BaseEntity;

import java.util.List;

public interface BaseEntitiesRepository extends BaseRepository<BaseEntity> {
	
	BaseEntity findByBaseEntityId(String baseEntityId);
	
	List<BaseEntity> findAllBaseEntities();
	
	List<BaseEntity> findAllByIdentifier(String identifier);
	
	List<BaseEntity> findAllByIdentifier(String identifierType, String identifier);
	
}
