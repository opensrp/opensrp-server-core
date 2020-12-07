package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Task;
import org.opensrp.domain.postgres.TaskMetadataExample;
import org.opensrp.repository.postgres.mapper.TaskMetadataMapper;
import org.opensrp.search.TaskSearchBean;

public interface CustomTaskMetadataMapper extends TaskMetadataMapper {
	
	Task selectByIdentifier(String identifier);

	List<Task> selectMany(@Param("example") TaskMetadataExample taskMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit);

	List<String> selectManyIds(@Param("example") TaskMetadataExample taskMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit );

	int countTasksByEntityIdAndPlanIdentifierAndCode(@Param("baseEntityId") String baseEntityId, @Param("jurisdiction") String jurisdiction ,@Param("planIdentifier") String planIdentifier, @Param("code") String code,
			@Param("statuses") List<String> statuses);

	Long countMany(@Param("example") TaskMetadataExample taskMetadataExample);

	List<org.smartregister.domain.Task> selectTasksBySearchBean(@Param("searchBean") TaskSearchBean searchBean,
			@Param("offset") int offset, @Param("limit") int limit);


	int selectTaskCount(@Param("searchBean") TaskSearchBean searchBean);
}
