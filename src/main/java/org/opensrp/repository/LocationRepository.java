package org.opensrp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.LocationDetail;
import org.opensrp.domain.StructureCount;
import org.smartregister.domain.PhysicalLocation;
import org.opensrp.domain.StructureDetails;
import org.opensrp.search.LocationSearchBean;
import org.smartregister.pathevaluator.dao.LocationDao;

public interface LocationRepository extends BaseRepository<PhysicalLocation>, LocationDao {

	PhysicalLocation getStructure(String id, boolean returnGeometry);

	PhysicalLocation get(String id, boolean returnGeometry);

	List<PhysicalLocation> findLocationsByServerVersion(long serverVersion);

	List<PhysicalLocation> findLocationsByNames(String locationNames, long serverVersion);

	List<PhysicalLocation> findStructuresByParentAndServerVersion(String parentId, long serverVersion);

	List<PhysicalLocation> findByEmptyServerVersion();

	List<PhysicalLocation> findStructuresByEmptyServerVersion();

	List<PhysicalLocation> getAllStructures();

	Collection<StructureDetails> findStructureAndFamilyDetails(double latitude, double longitude, double radius);

	/**
	 * This methods searches for jurisdictions using the parentId and location properties
	 * It returns the Geometry optionally if @param returnGeometry is set to true. 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param parentId string the parent id of the jurisdiction being searched
	 * @param properties map of location properties to filter with, each entry in map has property name and value
	 * @return jurisdictions matching the params 
	 */
	List<PhysicalLocation> findLocationsByProperties(boolean returnGeometry, String parentId,
			Map<String, String> properties);

	/**
	 * This methods searches for structures using the parentId and location properties
	 * It returns the Geometry optionally if @param returnGeometry is set to true. 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param parentId string the parent id of the structure being searched
	 * @param properties map of location properties to filter with, each entry in map has property name and value
	 * @return structures matching the params 
	 */
	List<PhysicalLocation> findStructuresByProperties(boolean returnGeometry, String parentId,
			Map<String, String> properties);

	/**
	 * This methods searches for locations using a list of provided location ids.
	 * It returns the Geometry optionally if @param returnGeometry is set to true.
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param ids list of location ids
	 * @return jurisdictions whose ids match the provided params
	 */
	List<PhysicalLocation> findLocationsByIds(boolean returnGeometry,	List<String> ids);

	/**
	 * This methods searches for a location and it's children using the provided location id
	 * It returns the Geometry optionally if @param returnGeometry is set to true.
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param id location id
	 * @param pageSize number of records to be returned
	 * @return location together with it's children whose id matches the provided param
	 */
	List<PhysicalLocation> findLocationByIdWithChildren(boolean returnGeometry,	String id, int pageSize);

    /**
	 * This methods searches for locations using a list of provided location ids.It returns location whose is in the list or whose parent is in list
	 * It returns the Geometry optionally if @param returnGeometry is set to true.
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param ids list of location ids
	 * @return jurisdictions whose ids match the provided params
	 */
	List<PhysicalLocation> findLocationsByIdsOrParentIds(boolean returnGeometry, List<String> ids);

    /**
     * This method fetches all structure Ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of structure ids to fetch
     * @return a list of structure Ids and last server version
     */
	Pair<List<String>, Long> findAllStructureIds(Long serverVersion, int limit);

	/**
	 * This method searches for location identifier and name using a plan identifier.
	 *
	 * @param planIdentifier identifier of the plan
	 * @return list of location details i.e. identifier and name
	 */
	Set<LocationDetail> findLocationDetailsByPlanId(String planIdentifier);

	/**
	 * This method searches for jurisdictions ordered by serverVersion ascending
	 *
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param serverVersion
	 * @param limit upper limit on number of jurisdictions to fetch
	 * @return list of jurisdictions
	 */
	List<PhysicalLocation> findAllLocations(boolean returnGeometry, Long serverVersion, int limit);

	/**
	 * This method searches for structures ordered by serverVersion ascending
	 *
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param serverVersion
	 * @param limit upper limit on number of structures to fetch
	 * @return list of structures
	 */
	List<PhysicalLocation> findAllStructures(boolean returnGeometry, Long serverVersion, int limit);

	/**
	 * This method fetches all location Ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of location ids to fetch
	 * @return a list of location Ids
	 */
	Pair<List<String>, Long> findAllLocationIds(Long serverVersion, int limit);
	
	List<PhysicalLocation> searchLocations(LocationSearchBean locationSearchBean);

	int countSearchLocations(LocationSearchBean locationSearchBean);

	/**
	 * Gets the parent locations inclusive of the location of the identifiers. 
	 * This returns the details of ancestors including locations the identifiers
	 * @param identifiers the identifiers of locations to get the parent locations
	 * @return the parent locations inclusive of the location of the identifiers 
	 */
	Set<LocationDetail> findParentLocationsInclusive(Set<String> identifiers);

	/**
	 * Gets the parent locations inclusive of the location of the identifiers.
	 * This returns the details of ancestors including locations the identifiers
	 * @param identifiers the identifiers of locations to get the parent locations
	 * @param returnTags Whether or not to return location tags
	 * @return the parent locations inclusive of the location of the identifiers
	 */
	Set<LocationDetail> findParentLocationsInclusive(Set<String> identifiers, boolean returnTags);


	/**
	 * This method is used to return a location based on the provided parameters
	 * @param identifier identifier of the location
	 * @param status status of the location
	 * @return returns a location matching the passed parameters
	 */
	PhysicalLocation findLocationByIdentifierAndStatus(String identifier, List<String> status, boolean returnGeometry);

	/**
	 * Gets the location primary key
	 * @param identifier of of the location
	 * @param isJurisdiction whether the to search for jurisdiction or structure
	 * @param version version of the location
	 * @return the numerical primary key of a jurisdiction
	 */
	public Long retrievePrimaryKey(String identifier, boolean isJurisdiction, int version);


	PhysicalLocation get(String id, boolean returnGeometry, int version);

	/**
	 * This method is used to return a count of structure based on the provided parameters
	 * @param parentId a string of comma separated ids for the parent locations
	 * @param serverVersion
	 * @return returns a count of structures matching the passed parameters
	 */
	Long countStructuresByParentAndServerVersion(String parentId, long serverVersion);

	/**
	 * This method is used to return a count of locations based on the provided parameters
	 * @param serverVersion
	 * @return returns a count of locations matching the passed parameters
	 */
	Long countLocationsByServerVersion(long serverVersion);

	/**
	 * This method is used to return a count of locations based on the provided parameters
	 * @param locationNames A string of comma separated location names
	 * @param serverVersion
	 * @return returns a count of locations matching the passed parameters
	 */
	Long countLocationsByNames(String locationNames, long serverVersion);

	/**
	 * Get location with all of its descendants.
	 *
	 * @param locationId location id of the root location
	 * @return chi
	 */
	Set<LocationDetail> findLocationWithDescendants(String locationId, boolean returnTags);

	/**
	 * This method returns a map containing a location identifier and a count of associated structures
	 * @param locationIds
	 * @return
	 */
	List<StructureCount> findStructureCountsForLocation(Set<String> locationIds);

	/**
	 * This methods searches for a location and it's children using the provided location ids
	 * It returns the Geometry optionally if @param returnGeometry is set to true.
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param identifiers location ids
	 * @param pageSize number of records to be returned
	 * @return location together with it's children whose id matches the provided param
	 */
	List<PhysicalLocation> findLocationByIdsWithChildren(boolean returnGeometry, Set<String> identifiers, int pageSize);
}
