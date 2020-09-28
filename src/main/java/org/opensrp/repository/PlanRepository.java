package org.opensrp.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.domain.PlanDefinition;

import java.util.List;

/**
 * Created by Vincent Karuri on 06/05/2019
 */
public interface PlanRepository extends BaseRepository<PlanDefinition> {

    List<PlanDefinition> getPlansByServerVersionAndOperationalAreas(Long serverVersion, List<String> operationalAreaIds, boolean experimental);

    Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, boolean isDeleted, Long minTime, Long maxTime);

    /**
     * This method searches for plans using a list of provided
     * plan identifiers and returns a subset of fields determined by the list of provided fields
     * If no plan identifier(s) are provided the method returns all available plans
     * If no fields are provided the method returns all the available fields
     * @param ids list of plan identifiers
     * @param fields list of fields to return
     * @return plan definitions whose identifiers match the provided params
     */
    List<PlanDefinition> getPlansByIdsReturnOptionalFields(List<String> ids, List<String> fields, boolean experimental);
    
    
    /**
     * Gets the plan primary key 
     * @param identifier of of the plan
     * @return the numerical primary key of a plan
     */
    public Long retrievePrimaryKey(String identifier);
	
	/** Gets the plans using the plan identifiers and whose server version is greater than or equal to server version
	 * @param planIdentifiers the plan identifiers
	 * @param serverVersion 
	 * @return plans with the identifiers and server version greater than or equal to server version param
	 */
	List<PlanDefinition> getPlansByIdentifiersAndServerVersion(List<String> planIdentifiers, Long serverVersion, boolean experimental);

	/**
	 *  This method searches for plans ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plans to fetch
	 * @return list of plan identifiers
	 */
	List<PlanDefinition> getAllPlans(Long serverVersion, int limit, boolean experimental);

	/**
	 * This method fetches all plan Ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plans to fetch
	 * @param isDeleted whether to return deleted plan ids
	 * @return a list of plan Ids  and the last server version
	 */
	Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, boolean isDeleted);

	/** Gets the plans using the plan identifiers and whose server version is greater than or equal to server version
	 * @param planIdentifiers the plan identifiers
	 * @param serverVersion
	 * @return plans with the identifiers and server version greater than or equal to server version param
	 */
	Long countPlansByIdentifiersAndServerVersion(List<String> planIdentifiers, Long serverVersion);

	List<PlanDefinition> getAllPlans(boolean experimental);
}
