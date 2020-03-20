package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.repository.postgres.mapper.EventMetadataMapper;

import java.util.List;

public interface CustomEventMetadataMapper extends EventMetadataMapper {
	
	Event selectByDocumentId(String documentId);
	
	List<Event> selectMany(EventMetadataExample eventMetadataExample);
	
	List<Event> selectManyWithRowBounds(@Param("example") EventMetadataExample example, @Param("offset") int offset,
	        @Param("limit") int limit);
	
	List<Event> selectNotInOpenMRSByServerVersion(@Param("from") long serverVersion, @Param("to") long calendar,
	        @Param("limit") int limit);
	
	List<Event> selectNotInOpenMRSByServerVersionAndType(@Param("eventType") String type, @Param("from") long serverVersion,
	        @Param("to") long calendar, @Param("limit") int limit);

	List<String> selectManyIds(@Param("example") EventMetadataExample example, @Param("offset") long offset,
			@Param("limit") int limit);
}
