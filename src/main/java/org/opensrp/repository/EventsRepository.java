package org.opensrp.repository;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.domain.Event;
import org.smartregister.pathevaluator.dao.EventDao;
import org.opensrp.search.EventSearchBean;

public interface EventsRepository extends BaseRepository<Event>, EventDao {
	
	List<Event> findAllByIdentifier(String identifier);
	
	List<Event> findAllByIdentifier(String identifierType, String identifier);
	
	Event findById(String id);
	
	Event findByFormSubmissionId(String formSubmissionId, boolean includeArchived);
	
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

	/**
	 * overloads {@link #findIdsByEventType(String, boolean, Long, int)} by adding date/time filters
	 * @param serverVersion
	 * @param limit
	 * @param minTime
	 * @param maxTime
	 * @return
	 */
	Pair<List<String>, Long> findIdsByEventType(String eventType, boolean isDeleted,
												Long serverVersion, int limit,
												Long minTime, Long maxTime);

	/**Updates an event
	 * @param entity the event to be updated
	 * @param allowArchived a flag that allows update of archived events
	 */
	void update(Event entity, boolean allowArchived);

	/**
	 * This method is used to return a count of locations based on the provided parameters
	 * @param eventSearchBean object containing params to search by
	 * @return returns a count of events matching the passed parameters
	 */
	Long countEvents(EventSearchBean eventSearchBean);

	/**
	 * Gets events baseEntityIds in a location
	 * @param locationId location id
	 * @return baseEntityIds in a location
	 */
	List<String> findBaseEntityIdsByLocation(String locationId);
	
}
