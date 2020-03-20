package org.opensrp.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.Event;
import org.opensrp.search.EventSearchBean;

import java.util.Calendar;
import java.util.List;

public interface EventsRepository extends BaseRepository<Event> {
	
	List<Event> findAllByIdentifier(String identifier);
	
	List<Event> findAllByIdentifier(String identifierType, String identifier);
	
	Event findById(String id);
	
	Event findByFormSubmissionId(String formSubmissionId);
	
	List<Event> findByBaseEntityId(String baseEntityId);
	
	Event findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId);
	
	List<Event> findByBaseEntityAndType(String baseEntityId, String eventType);
	
	List<Event> findEvents(EventSearchBean eventSearchBean);
	
	List<Event> findEventsByDynamicQuery(String query);
	
	List<Event> findByServerVersion(long serverVersion);
	
	List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar);
	
	List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar);
	
	List<Event> findByClientAndConceptAndDate(String baseEntityId, String concept, String conceptValue, String dateFrom,
	        String dateTo);
	
	List<Event> findByBaseEntityIdAndConceptParentCode(String baseEntityId, String concept, String parentCode);
	
	List<Event> findByConceptAndValue(String concept, String conceptValue);
	
	List<Event> findByEmptyServerVersion();
	
	List<Event> findEvents(EventSearchBean eventSearchBean, String sortBy, String sortOrder, int limit);
	
	List<Event> findEventByEventTypeBetweenTwoDates(String eventType);
	
	List<Event> findByProvider(String provider);

	/**
	 * This method searches for event ids filtered by eventType,
	 * the date they were deleted and server version
	 *
	 * @param eventType used to filter the event ids
	 * @param isDeleted whether to return deleted event ids
	 * @param serverVersion
	 * @param limit upper limit on number of tasks ids to fetch
	 * @return a list of event ids and last server version
	 */
	Pair<List<String>, Long> findIdsByEventType(String eventType, boolean isDeleted, Long serverVersion, int limit);
	
}
