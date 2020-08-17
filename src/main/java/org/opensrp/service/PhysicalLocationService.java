package org.opensrp.service;

import static org.opensrp.domain.StructureCount.STRUCTURE_COUNT;
import static org.smartregister.domain.LocationProperty.PropertyStatus.ACTIVE;
import static org.smartregister.domain.LocationProperty.PropertyStatus.PENDING_REVIEW;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.domain.LocationDetail;
import org.opensrp.domain.StructureCount;
import org.opensrp.domain.StructureDetails;
import org.opensrp.repository.LocationRepository;
import org.opensrp.search.LocationSearchBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class PhysicalLocationService {
	
	private static Logger logger = LoggerFactory.getLogger(PhysicalLocationService.class.toString());
	
	private LocationRepository locationRepository;
	
	private final boolean DEFAULT_RETURN_BOOLEAN = true;
	
	@Autowired
	public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}
	
	public PhysicalLocation getLocation(String id, boolean returnGeometry) {
		return locationRepository.get(id, returnGeometry);
	}
	
	public PhysicalLocation getLocation(String id, boolean returnGeometry, int version) {
		return locationRepository.get(id, returnGeometry, version);
	}
	
	public PhysicalLocation getStructure(String id, boolean returnGeometry) {
		return locationRepository.getStructure(id, returnGeometry);
	}
	
	public List<PhysicalLocation> getAllLocations() {
		return locationRepository.getAll();
	}
	
	public void addOrUpdate(PhysicalLocation physicalLocation) {
		if (StringUtils.isBlank(physicalLocation.getId()))
			throw new IllegalArgumentException("id not specified");
		if ((physicalLocation.isJurisdiction() && getLocation(physicalLocation.getId(), DEFAULT_RETURN_BOOLEAN) == null)
		        || (!physicalLocation.isJurisdiction()
		                && getStructure(physicalLocation.getId(), DEFAULT_RETURN_BOOLEAN) == null)) {
			add(physicalLocation);
		} else {
			update(physicalLocation);
		}
	}
	
	public void add(PhysicalLocation physicalLocation) {
		if (StringUtils.isBlank(physicalLocation.getId()))
			throw new IllegalArgumentException("id not specified");
		physicalLocation.setServerVersion(null);
		locationRepository.add(physicalLocation);
	}
	
	public void update(PhysicalLocation physicalLocation) {
		if (StringUtils.isBlank(physicalLocation.getId()))
			throw new IllegalArgumentException("id not specified");
		physicalLocation.setServerVersion(null);
		PhysicalLocation existingEntity = locationRepository.findLocationByIdentifierAndStatus(physicalLocation.getId(),
		    Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()), true);
		boolean locationHasNoUpdates = isGeometryCoordsEqual(physicalLocation, existingEntity);
		if (locationHasNoUpdates || !physicalLocation.isJurisdiction() || existingEntity == null) {
			locationRepository.update(physicalLocation);
		} else {
			//make existing location inactive
			existingEntity.getProperties().setStatus(LocationProperty.PropertyStatus.INACTIVE);
			existingEntity.setServerVersion(null);
			locationRepository.update(existingEntity);
			
			// create new location
			//increment location version
			int newVersion = existingEntity.getProperties().getVersion() + 1;
			physicalLocation.getProperties().setVersion(newVersion);
			
			locationRepository.add(physicalLocation);
		}
		
	}
	
	public List<PhysicalLocation> findLocationsByServerVersion(long serverVersion) {
		return locationRepository.findLocationsByServerVersion(serverVersion);
	}
	
	public List<PhysicalLocation> findLocationsByNames(String locationNames, long serverVersion) {
		return locationRepository.findLocationsByNames(locationNames, serverVersion);
	}
	
	public List<PhysicalLocation> findStructuresByParentAndServerVersion(String parentId, long serverVersion) {
		if (StringUtils.isBlank(parentId))
			throw new IllegalArgumentException("parentId not specified");
		return locationRepository.findStructuresByParentAndServerVersion(parentId, serverVersion);
	}
	
	public void addServerVersion() {
		try {
			List<PhysicalLocation> locations = locationRepository.findByEmptyServerVersion();
			logger.info("RUNNING addServerVersion Jurisdiction locations size: " + locations.size());
			setServerVersion(locations, true);
			List<PhysicalLocation> structures = locationRepository.findStructuresByEmptyServerVersion();
			logger.info("RUNNING addServerVersion structures size: " + structures.size());
			setServerVersion(structures, false);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void setServerVersion(List<PhysicalLocation> locations, boolean isJurisdiction) {
		long currentTimeMillis = System.currentTimeMillis();
		for (PhysicalLocation location : locations) {
			try {
				Thread.sleep(1);
				location.setServerVersion(currentTimeMillis);
				location.setJurisdiction(isJurisdiction);
				locationRepository.update(location);
				currentTimeMillis += 1;
			}
			catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public Set<String> saveLocations(List<PhysicalLocation> locations, boolean isJurisdiction) {
		Set<String> locationsWithErrors = new HashSet<>();
		for (PhysicalLocation location : locations) {
			try {
				location.setJurisdiction(isJurisdiction);
				addOrUpdate(location);
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
				locationsWithErrors.add(location.getId());
			}
		}
		return locationsWithErrors;
		
	}
	
	public Collection<StructureDetails> findStructuresWithinRadius(double latitude, double longitude, double radius) {
		return locationRepository.findStructureAndFamilyDetails(latitude, longitude, radius);
	}
	
	/**
	 * This methods searches for jurisdictions using the parentId and location properties It returns
	 * the Geometry optionally if @param returnGeometry is set to true.
	 * 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param parentId string the parent id of the jurisdiction being searched
	 * @param properties map of location properties to filter with, each entry in map has property
	 *            name and value
	 * @return jurisdictions matching the params
	 * @see org.opensrp.repository.LocationRepository#findLocationsByProperties(boolean, String,
	 *      Map)
	 */
	public List<PhysicalLocation> findLocationsByProperties(boolean returnGeometry, String parentId,
	        Map<String, String> properties) {
		return locationRepository.findLocationsByProperties(returnGeometry, parentId, properties);
	}
	
	/**
	 * This methods searches for structures using the parentId and location properties It returns
	 * the Geometry optionally if @param returnGeometry is set to true.
	 * 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param parentId string the parent id of the structure being searched
	 * @param properties map of location properties to filter with, each entry in map has property
	 *            name and value
	 * @return structures matching the params
	 * @see org.opensrp.repository.LocationRepository#findStructuresByProperties(boolean, String,
	 *      Map)
	 */
	public List<PhysicalLocation> findStructuresByProperties(boolean returnGeometry, String parentId,
	        Map<String, String> properties) {
		return locationRepository.findStructuresByProperties(returnGeometry, parentId, properties);
	}
	
	/**
	 * This methods provides an API endpoint that searches for locations using a list of provided
	 * location ids. It returns the Geometry optionally if @param returnGeometry is set to true.
	 * 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param ids list of location ids
	 * @return jurisdictions whose ids match the provided params
	 */
	public List<PhysicalLocation> findLocationsByIds(boolean returnGeometry, List<String> ids) {
		return locationRepository.findLocationsByIds(returnGeometry, ids);
	}
	
	/**
	 * This methods searches for locations using a list of provided location ids.It returns location
	 * whose is in the list or whose parent is in list It returns the Geometry optionally if @param
	 * returnGeometry is set to true.
	 * 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param ids list of location ids
	 * @return jurisdictions whose ids match the provided params
	 */
	public List<PhysicalLocation> findLocationsByIdsOrParentIds(boolean returnGeometry, List<String> ids) {
		return locationRepository.findLocationsByIdsOrParentIds(returnGeometry, ids);
	}
	
	/**
	 * This methods searches for a location and it's children using the provided location id It
	 * returns the Geometry optionally if @param returnGeometry is set to true.
	 * 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param id location id
	 * @param pageSize number of records to be returned
	 * @return location together with it's children whose id matches the provided param
	 */
	public List<PhysicalLocation> findLocationByIdWithChildren(boolean returnGeometry, String id, int pageSize) {
		return locationRepository.findLocationByIdWithChildren(returnGeometry, id, pageSize);
	}
	
	/**
	 * This methods searches for a location and it's children using the provided location Ids
	 * returns the Geometry optionally if @param returnGeometry is set to true.
	 * 
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param locationIds location ids
	 * @param pageSize number of records to be returned
	 * @return location together with it's children whose id matches the provided param
	 */
	public List<PhysicalLocation> findLocationByIdsWithChildren(boolean returnGeometry, Set<String> locationIds,
	        int pageSize) {
		return locationRepository.findLocationByIdsWithChildren(returnGeometry, locationIds, pageSize);
	}
	
	/**
	 * This method searches for all structure ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of structure ids to fetch
	 * @return a list of structure ids as well as the lastServerVersion
	 */
	public Pair<List<String>, Long> findAllStructureIds(Long serverVersion, int limit) {
		return locationRepository.findAllStructureIds(serverVersion, limit);
	}
	
	/**
	 * This method searches for location identifier and name using a plan identifier.
	 *
	 * @param planIdentifier identifier of the plan
	 * @return list of location details i.e. identifier and name
	 */
	public Set<LocationDetail> findLocationDetailsByPlanId(String planIdentifier) {
		return locationRepository.findLocationDetailsByPlanId(planIdentifier);
	}
	
	/**
	 * This method searches for jurisdictions ordered by serverVersion ascending
	 *
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param serverVersion
	 * @param limit upper limit on number of jurisdictions to fetch
	 * @return list of jurisdictions
	 */
	public List<PhysicalLocation> findAllLocations(boolean returnGeometry, Long serverVersion, int limit) {
		return locationRepository.findAllLocations(returnGeometry, serverVersion, limit);
	};
	
	/**
	 * This method searches for structures ordered by serverVersion ascending
	 *
	 * @param returnGeometry boolean which controls if geometry is returned
	 * @param limit upper limit on number of structures to fetch
	 * @return list of structures
	 */
	public List<PhysicalLocation> findAllStructures(boolean returnGeometry, Long serverVersion, int limit) {
		return locationRepository.findAllStructures(returnGeometry, serverVersion, limit);
	};
	
	/**
	 * This method searches for all location ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of location ids to fetch
	 * @return a list of location ids
	 */
	public Pair<List<String>, Long> findAllLocationIds(Long serverVersion, int limit) {
		return locationRepository.findAllLocationIds(serverVersion, limit);
	}
	
	public List<PhysicalLocation> searchLocations(LocationSearchBean locationSearchBean) {
		return locationRepository.searchLocations(locationSearchBean);
	}
	
	public int countSearchLocations(LocationSearchBean locationSearchBean) {
		return locationRepository.countSearchLocations(locationSearchBean);
	}
	
	/**
	 * Gets the location tree for the location identifiers. This returns the details of ancestors
	 * including the identifiers
	 * 
	 * @param identifiers the id of locations to get location hierarchy
	 * @param returnStructureCount whether to return structure counts for the jurisdictions
	 * @param returnTags whether to return loction tags
	 * @return the location hierarchy/tree of the identifiers
	 */
	public LocationTree buildLocationHierachy(Set<String> identifiers, boolean returnStructureCount, boolean returnTags) {
		LocationTree locationTree = new LocationTree();
		Set<LocationDetail> locationDetails = locationRepository.findParentLocationsInclusive(identifiers, returnTags);
		locationTree.buildTreeFromList(getLocations(locationDetails, returnStructureCount));
		return locationTree;
	}
	
	private Location getLocationFromDetail(LocationDetail locationDetail, Map<String, LocationDetail> locationMap,
	        boolean returnStructureCounts, Map<String, Integer> cumulativeCountsMap) {
		Location location = new Location();
		location.setLocationId(locationDetail.getIdentifier());
		location.setName(locationDetail.getName());
		location.setVoided(locationDetail.isVoided());
		if (locationDetail.getTags() != null) {
			location.setTags(new HashSet<>(Arrays.asList(locationDetail.getTags().split(","))));
		}
		LocationDetail parent = locationMap.get(locationDetail.getParentId());
		if (parent != null) {
			location.setParentLocation(new Location().withLocationId(parent.getIdentifier()));
		}
		location.addAttribute("geographicLevel", locationDetail.getGeographicLevel());
		
		if (returnStructureCounts) {
			location.addAttribute(STRUCTURE_COUNT, cumulativeCountsMap.get(location.getLocationId()));
		}
		return location;
	}
	
	private void populateCumulativeCountsMap(Set<LocationDetail> locationDetails, Map<String, Integer> cumulativeCountsMap,
	        Map<String, StructureCount> structureCountMap) {
		
		for (LocationDetail locationDetail : locationDetails) {
			StructureCount structureCount = structureCountMap.get(locationDetail.getIdentifier());
			if (structureCount != null) { //only locations with structure counts
				int updatedCount = cumulativeCountsMap.get(locationDetail.getIdentifier()) == null
				        ? structureCount.getCount()
				        : cumulativeCountsMap.get(locationDetail.getIdentifier()) + structureCount.getCount();
				cumulativeCountsMap.put(locationDetail.getIdentifier(), updatedCount);
			} else if (cumulativeCountsMap.get(locationDetail.getIdentifier()) == null) {
				cumulativeCountsMap.put(locationDetail.getIdentifier(), 0);
			}
			
			if (locationDetail.getParentId() != null) {
				// init parent location map value
				if (cumulativeCountsMap.get(locationDetail.getParentId()) == null) {
					cumulativeCountsMap.put(locationDetail.getParentId(), 0);
				}
				
				// update parent location structure count
				int updatedCount = cumulativeCountsMap.get(locationDetail.getParentId())
				        + cumulativeCountsMap.get(locationDetail.getIdentifier());
				cumulativeCountsMap.put(locationDetail.getParentId(), updatedCount);
			}
			
		}
		
	}
	
	private List<Location> getLocations(Set<LocationDetail> locationDetails, boolean returnStructureCounts) {
		/* @formatter:off */
		List<StructureCount> structureCountsForLocation = null;
		Map<String, StructureCount> structureCountMap = null;
		Map<String, Integer> cumulativeCountsMap = new HashMap<>();
		Map<String, LocationDetail> locationMap = locationDetails
				.stream()
				.collect(Collectors.toMap(LocationDetail::getIdentifier, (entry) -> entry));

		if (returnStructureCounts) {
			structureCountsForLocation = locationRepository
					.findStructureCountsForLocation(locationMap.keySet());

			structureCountMap = structureCountsForLocation
					.stream()
					.collect(Collectors.toMap(StructureCount::getParentId, (entry) -> entry));

				populateCumulativeCountsMap(locationDetails, cumulativeCountsMap, structureCountMap);

		}

		return locationDetails
				.stream()
				.map(location -> getLocationFromDetail(location, locationMap, returnStructureCounts, cumulativeCountsMap))
				.collect(Collectors.toList());


		/* @formatter:on */
	}
	
	/**
	 * This method is used to return a count of structure based on the provided parameters
	 * 
	 * @param parentId id for the parent location
	 * @param serverVersion
	 * @return returns a count of structures matching the passed parameters
	 */
	public Long countStructuresByParentAndServerVersion(String parentId, long serverVersion) {
		return locationRepository.countStructuresByParentAndServerVersion(parentId, serverVersion);
	}
	
	/**
	 * This method is used to return a count of locations based on the provided parameters
	 * 
	 * @param serverVersion
	 * @return returns a count of locations matching the passed parameters
	 */
	public Long countLocationsByServerVersion(long serverVersion) {
		return locationRepository.countLocationsByServerVersion(serverVersion);
	};
	
	/**
	 * This method is used to return a count of locations based on the provided parameters
	 * 
	 * @param locationNames A string of comma separated location names
	 * @param serverVersion
	 * @return returns a count of locations matching the passed parameters
	 */
	public Long countLocationsByNames(String locationNames, long serverVersion) {
		return locationRepository.countLocationsByNames(locationNames, serverVersion);
	};
	
	/**
	 * This method checks whether the coordinates contained in the locations Geometry are equal
	 * 
	 * @param newEntity location entity
	 * @param existingEntity location entity
	 * @return
	 */
	public boolean isGeometryCoordsEqual(PhysicalLocation newEntity, PhysicalLocation existingEntity) {
		if (newEntity == null || existingEntity == null) {
			return false;
		}
		JsonElement newGeometryCoordsElement = JsonParser.parseString(newEntity.getGeometry().getCoordinates().toString());
		JsonElement existingGeometryCoordsElement = JsonParser
		        .parseString(existingEntity.getGeometry().getCoordinates().toString());
		return newGeometryCoordsElement.equals(existingGeometryCoordsElement);
	}
	
	public LocationTree buildLocationHierachyFromLocation(String locationId, boolean returnStructureCount) {
		return buildLocationHierachyFromLocation(locationId, false, returnStructureCount);
	}
	
	/**
	 * Build full location tree with passed location id as tree root
	 *
	 * @param locationId id of the root location
	 * @return full location hierarchy from passed location plus all of its descendants
	 */
	public LocationTree buildLocationHierachyFromLocation(String locationId, boolean returnTags,
	        boolean returnStructureCount) {
		LocationTree locationTree = new LocationTree();
		Set<LocationDetail> locationDetails = locationRepository.findLocationWithDescendants(locationId, returnTags);
		locationTree.buildTreeFromList(getLocations(locationDetails, returnStructureCount));
		return locationTree;
	}
	
}
