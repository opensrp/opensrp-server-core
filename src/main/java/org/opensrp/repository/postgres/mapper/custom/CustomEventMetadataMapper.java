package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.repository.postgres.mapper.EventMetadataMapper;

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
	
	/** Gets primary Key
	 * @param eventMetadataExample
	 * @return the primary key
	 */
	Long selectPrimaryKey(EventMetadataExample eventMetadataExample);

	/**
	 * Gets events baseEntityIds in matching the criteria
	 * @param example criteria
	 * @return baseEntityIds matching criteria
	 */
	List<String> selectManyBaseEntityIds(@Param("example") EventMetadataExample example);

	List<Event> selectByBaseEntityIdAndPlanIdentifier(@Param("baseEntityId") String baseEntityId, @Param("planIdentifier") String planIdentifier);

	List<Event> selectByPlanIdentifierAndEventType(@Param("planIdentifier") String planIdentifier, @Param("eventType") String eventType,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
