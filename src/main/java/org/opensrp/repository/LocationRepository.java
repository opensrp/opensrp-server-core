package org.opensrp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.LocationDetail;
import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.StructureDetails;

public interface LocationRepository extends BaseRepository<PhysicalLocation> {

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
     * Gets the location primary key 
     * @param identifier of of the plan
     * @param isJurisdiction whether the to search for jurisdiction or structure
     * @return the numerical primary key of a jurisdiction
     */
    public Long retrievePrimaryKey(String identifier, boolean isJurisdiction);

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
	Pair findAllStructureIds(Long serverVersion, int limit);

	/**
	 * This method searches for location identifier and name using a plan identifier.
	 *
	 * @param planIdentifier identifier of the plan
	 * @return list of location details i.e. identifier and name
	 */
	List<LocationDetail> findLocationDetailsByPlanId(String planIdentifier);

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
	Pair findAllLocationIds(Long serverVersion, int limit);

}
