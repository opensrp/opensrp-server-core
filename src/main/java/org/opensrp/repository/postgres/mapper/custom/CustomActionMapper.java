package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Action;
import org.opensrp.repository.postgres.mapper.ActionMapper;

import java.util.List;

public interface CustomActionMapper extends ActionMapper {
	
	int insertSelectiveAndSetId(Action action);
	
	Action selectByDocumentId(String documentId);
	
	List<Long> selectIdsByTarget(String actionTarget);
	
	List<Action> selectNotExpired(@Param("offset") int offset, @Param("limit") int limit);
}
