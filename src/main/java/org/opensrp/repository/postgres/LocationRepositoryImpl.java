package org.opensrp.repository.postgres;

import static org.smartregister.domain.LocationProperty.PropertyStatus.ACTIVE;
import static org.smartregister.domain.LocationProperty.PropertyStatus.PENDING_REVIEW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.LocationDetail;
import org.opensrp.domain.LocationTagMap;
import org.opensrp.domain.Report;
import org.opensrp.domain.StructureCount;
import org.opensrp.domain.StructureDetails;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.LocationMetadata;
import org.opensrp.domain.postgres.LocationMetadataExample;
import org.opensrp.domain.postgres.LocationMetadataExample.Criteria;
import org.opensrp.domain.postgres.Structure;
import org.opensrp.domain.postgres.StructureFamilyDetails;
import org.opensrp.domain.postgres.StructureMetadata;
import org.opensrp.domain.postgres.StructureMetadataExample;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationMetadataMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStructureMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStructureMetadataMapper;
import org.opensrp.search.LocationSearchBean;
import org.opensrp.service.LocationTagService;
import org.smartregister.converters.LocationConverter;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LocationRepositoryImpl extends BaseRepositoryImpl<PhysicalLocation> implements LocationRepository {
	
	private static final String LOCATION_SEQUENCE = "core.location_server_version_seq";
	
	private static final String STRUCTURE_SEQUENCE = "core.structure_server_version_seq";
	
	@Autowired
	private CustomLocationMapper locationMapper;
	
	@Autowired
	private CustomLocationMetadataMapper locationMetadataMapper;
	
	@Autowired
	private CustomStructureMapper structureMapper;
	
	@Autowired
	private CustomStructureMetadataMapper structureMetadataMapper;
	
	@Autowired
	private LocationTagService locationTagService;
	
	@Override
	public PhysicalLocation get(String id) {
		return convert(locationMetadataMapper.findById(id, true));
	}
	
	@Override
	public PhysicalLocation get(String id, boolean returnGeography) {
		return convert(locationMetadataMapper.findById(id, returnGeography));
	}
	
	@Override
	public PhysicalLocation getStructure(String id, boolean returnGeography) {
		return convert(structureMetadataMapper.findById(id, returnGeography));
	}
	
	@Override
	@Transactional
	public void add(PhysicalLocation entity) {
		if (getUniqueField(entity) == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { // PhysicalLocation already added
			return;
		}
		
		if (entity.isJurisdiction())
			addLocation(entity);
		else
			addStructure(entity);
		
	}
	
	private void updateLocationServerVersion(org.opensrp.domain.postgres.Location pgLocation, PhysicalLocation entity) {
		long serverVersion = locationMapper.selectServerVersionByPrimaryKey(pgLocation.getId());
		entity.setServerVersion(serverVersion);
		
		int rowsAffected = locationMapper.updateByPrimaryKeySelective(pgLocation);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}
	
	private void updateStructureServerVersion(org.opensrp.domain.postgres.Structure pgStructure, PhysicalLocation entity) {
		long serverVersion = structureMapper.selectServerVersionByPrimaryKey(pgStructure.getId());
		entity.setServerVersion(serverVersion);
		
		int rowsAffected = structureMapper.updateByPrimaryKeySelective(pgStructure);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}
	
	
	private void addLocation(PhysicalLocation entity) {
		
		Location pgLocation = convert(entity, null);
		if (pgLocation == null) {
			return;
		}
		
		int rowsAffected = locationMapper.insertSelectiveAndSetId(pgLocation);
		
		if (rowsAffected < 1 || pgLocation.getId() == null) {
			throw new IllegalStateException();
		}
		
		updateLocationServerVersion(pgLocation, entity);
		
		LocationMetadata locationMetadata = createMetadata(entity, pgLocation.getId());
		
		locationMetadataMapper.insertSelective(locationMetadata);
		saveLocationTag(entity, pgLocation.getId(), false);
	}
	
	private void addStructure(PhysicalLocation entity) {
		
		Structure pgStructure = convertStructure(entity, null);
		if (pgStructure == null) {
			return;
		}
		
		int rowsAffected = structureMapper.insertSelectiveAndSetId(pgStructure);
		if (rowsAffected < 1 || pgStructure.getId() == null) {
			throw new IllegalStateException();
		}
		
		updateStructureServerVersion(pgStructure, entity);
		
		StructureMetadata structureMetadata = createStructureMetadata(entity, pgStructure.getId());
		
		structureMetadataMapper.insertSelective(structureMetadata);
		
	}
	
	@Override
	@Transactional
	public void update(PhysicalLocation entity) {
		if (getUniqueField(entity) == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // PhysicalLocation does not exist
			return;
		}
		if (entity.isJurisdiction())
			updateLocation(entity, id);
		else
			updateStructure(entity, id);
		
	}
	
	private void updateLocation(PhysicalLocation entity, Long id) {
		Location pgLocation = convert(entity, id);
		if (pgLocation == null) {
			return;
		}
		
		
		int rowsAffected = locationMapper.updateByPrimaryKeyAndGenerateServerVersion(pgLocation);
		if (rowsAffected < 1) {
			return;
		}
		
		updateLocationServerVersion(pgLocation, entity);
		
		
		LocationMetadata locationMetadata = createMetadata(entity, pgLocation.getId());
		
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andLocationIdEqualTo(id);
		LocationMetadata metadata = locationMetadataMapper.selectByExample(locationMetadataExample).get(0);
		locationMetadata.setId(metadata.getId());
		locationMetadata.setDateCreated(metadata.getDateCreated());
		locationMetadataMapper.updateByPrimaryKey(locationMetadata);
		saveLocationTag(entity, pgLocation.getId(), true);
	}
	
	private void updateStructure(PhysicalLocation entity, Long id) {
		Structure pgStructure = convertStructure(entity, id);
		if (pgStructure == null) {
			return;
		}
		
		
		int rowsAffected = structureMapper.updateByPrimaryKeyAndGenerateServerVersion(pgStructure);
		if (rowsAffected < 1) {
			return;
		}
		
		updateStructureServerVersion(pgStructure, entity);
		
		StructureMetadata structureMetadata = createStructureMetadata(entity, pgStructure.getId());
		
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andStructureIdEqualTo(id);
		StructureMetadata metadata = structureMetadataMapper.selectByExample(structureMetadataExample).get(0);
		structureMetadata.setId(metadata.getId());
		structureMetadata.setDateCreated(metadata.getDateCreated());
		structureMetadataMapper.updateByPrimaryKey(structureMetadata);
	}
	
	@Override
	public List<PhysicalLocation> getAll() {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		List<Location> locations = locationMetadataMapper.selectMany(locationMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	@Override
	public List<PhysicalLocation> getAllStructures() {
		List<Structure> structures = structureMetadataMapper.selectMany(new StructureMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convertStructures(structures);
	}
	
	@Override
	@Transactional
	public void safeRemove(PhysicalLocation entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		if (entity.isJurisdiction()) {
			LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
			locationMetadataExample.createCriteria().andLocationIdEqualTo(id);
			int rowsAffected = locationMetadataMapper.deleteByExample(locationMetadataExample);
			if (rowsAffected < 1) {
				return;
			}
			
			locationMapper.deleteByPrimaryKey(id);
		} else {
			StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
			structureMetadataExample.createCriteria().andStructureIdEqualTo(id);
			int rowsAffected = structureMetadataMapper.deleteByExample(structureMetadataExample);
			if (rowsAffected < 1) {
				return;
			}
			
			structureMapper.deleteByPrimaryKey(id);
		}
		
	}
	
	@Override
	public List<PhysicalLocation> findLocationsByServerVersion(long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		locationMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		List<Location> locations = locationMetadataMapper.selectMany(locationMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	@Override
	public List<PhysicalLocation> findLocationsByNames(String locationNames, long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria()
		        .andNameIn(Arrays.asList(org.apache.commons.lang.StringUtils.split(locationNames, ",")))
		        .andServerVersionGreaterThanOrEqualTo(serverVersion)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		locationMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		List<Location> locations = locationMetadataMapper.selectMany(locationMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	@Override
	public List<PhysicalLocation> findStructuresByParentAndServerVersion(String parentIds, long serverVersion) {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria()
		        .andParentIdIn(Arrays.asList(org.apache.commons.lang.StringUtils.split(parentIds, ",")))
		        .andServerVersionGreaterThanOrEqualTo(serverVersion);
		structureMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		List<Structure> locations = structureMetadataMapper.selectMany(structureMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convertStructures(locations);
	}
	
	@Override
	public List<PhysicalLocation> findByEmptyServerVersion() {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andServerVersionEqualTo(0l);
		locationMetadataExample.or(locationMetadataExample.createCriteria().andServerVersionIsNull());
		List<Location> locations = locationMetadataMapper.selectMany(locationMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	@Override
	public List<PhysicalLocation> findStructuresByEmptyServerVersion() {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andServerVersionEqualTo(0l);
		structureMetadataExample.or(structureMetadataExample.createCriteria().andServerVersionIsNull());
		List<Structure> locations = structureMetadataMapper.selectMany(structureMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convertStructures(locations);
	}
	
	@Override
	public Collection<StructureDetails> findStructureAndFamilyDetails(double latitude, double longitude, double radius) {
		List<StructureFamilyDetails> pgList = structureMapper.selectStructureAndFamilyWithinRadius(latitude, longitude,
		    radius);
		Map<String, StructureDetails> structureDetails = new HashMap<>();
		for (StructureFamilyDetails detail : pgList) {
			StructureDetails structure;
			if (!structureDetails.containsKey(detail.getId())) {
				structure = new StructureDetails(detail.getId(), detail.getParentId(), detail.getType());
				
				structureDetails.put(detail.getId(), structure);
			} else {
				structure = structureDetails.get(detail.getId());
			}
			if (StringUtils.isNotBlank(detail.getBaseEntityId())) {
				if ("Family".equalsIgnoreCase(detail.getLastName()))
					structure.setFamilyId(detail.getBaseEntityId());
				else
					structure.getFamilyMembers().add(detail.getBaseEntityId());
			}
		}
		return structureDetails.values();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationsByProperties(boolean returnGeometry, String parentId,
	        Map<String, String> properties) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		if (StringUtils.isNotBlank(parentId)) {
			locationMetadataExample.createCriteria().andParentIdEqualTo(parentId);
		}
		List<Location> locations = locationMetadataMapper.selectManyByProperties(locationMetadataExample, properties,
		    returnGeometry, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findStructuresByProperties(boolean returnGeometry, String parentId,
	        Map<String, String> properties, int limit) {
		int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		if (StringUtils.isNotBlank(parentId)) {
			structureMetadataExample.createCriteria().andParentIdEqualTo(parentId);
		}
		List<Location> locations = structureMetadataMapper.selectManyByProperties(structureMetadataExample, properties,
		    returnGeometry, 0, fetchLimit);
		return convert(locations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findStructuresByProperties(boolean returnGeometry, String parentId,
			Map<String, String> properties) {
		return findStructuresByProperties(returnGeometry,parentId,properties,DEFAULT_FETCH_SIZE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationsByIds(boolean returnGeometry, List<String> ids, Long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		
		Criteria criteria = locationMetadataExample.createCriteria().andGeojsonIdIn(ids)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		if (serverVersion != null) {
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
		}
		
		List<Location> locations = locationMetadataMapper.selectManyWithOptionalGeometry(locationMetadataExample,
		    returnGeometry, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationsByIdsOrParentIds(boolean returnGeometry, List<String> ids) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		
		locationMetadataExample.createCriteria().andGeojsonIdIn(ids)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		
		locationMetadataExample.or(locationMetadataExample.createCriteria().andParentIdIn(ids)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name())));
		List<Location> locations = locationMetadataMapper.selectManyWithOptionalGeometry(locationMetadataExample,
		    returnGeometry, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}
	
	@Override
	public Pair<List<String>, Long> findAllStructureIds(Long serverVersion, int limit) {
		Long lastServerVersion = null;
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		structureMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		return getStructuresListLongPair(limit, lastServerVersion, structureMetadataExample);
	}
	
	@Override
	public Pair<List<String>, Long> findAllStructureIds(Long serverVersion, int limit, Date fromDate, Date toDate) {
		if (fromDate == null && toDate == null) {
			return findAllStructureIds(serverVersion, limit);
		} else {
			Long lastServerVersion = null;
			StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
			structureMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
			StructureMetadataExample.Criteria criteria = structureMetadataExample.createCriteria();
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
			if (toDate != null && fromDate != null) {
				criteria.andDateCreatedBetween(fromDate, toDate);
			} else if (fromDate != null) {
				criteria.andDateCreatedGreaterThanOrEqualTo(fromDate);
			} else {
				criteria.andDateCreatedLessThanOrEqualTo(toDate);
			}
			
			return getStructuresListLongPair(limit, lastServerVersion, structureMetadataExample);
		}
		
	}
	
	private Pair<List<String>, Long> getStructuresListLongPair(int limit, Long lastServerVersion,
	        StructureMetadataExample structureMetadataExample) {
		int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
		Long serverVersion = lastServerVersion;
		StructureMetadataExample example = structureMetadataExample;
		List<String> structureIdentifiers = structureMetadataMapper.selectManyIds(example, 0, fetchLimit);
		
		if (structureIdentifiers != null && !structureIdentifiers.isEmpty()) {
			example = new StructureMetadataExample();
			example.createCriteria()
			        .andGeojsonIdEqualTo(structureIdentifiers.get(structureIdentifiers.size() - 1));
			List<StructureMetadata> structureMetaDataList = structureMetadataMapper
			        .selectByExample(example);

			serverVersion = structureMetaDataList != null && !structureMetaDataList.isEmpty()
			        ? structureMetaDataList.get(0).getServerVersion()
			        : 0;
		}
		
		return Pair.of(structureIdentifiers, serverVersion);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LocationDetail> findLocationDetailsByPlanId(String planIdentifier) {
		
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		return locationMetadataMapper.selectDetailsByPlanId(locationMetadataExample, planIdentifier);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationByIdWithChildren(boolean returnGeometry, String id, int pageSize) {
		return findLocationByIdsWithChildren(returnGeometry, Collections.singleton(id), pageSize);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationByIdsWithChildren(boolean returnGeometry, Set<String> identifiers,
	        int pageSize) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		if (identifiers == null) {
			return null;
		}
		
		int limit = Math.abs(pageSize);
		List<Location> locations = locationMetadataMapper.selectWithChildren(locationMetadataExample, returnGeometry,
		    identifiers, 0, limit);
		return convert(locations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findAllLocations(boolean returnGeometry, Long serverVersion, int limit) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		locationMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		
		List<Location> locations = locationMetadataMapper.selectManyWithOptionalGeometry(locationMetadataExample,
		    returnGeometry, 0, limit);
		return convert(locations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countAllLocations(Long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion)
				.andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		return locationMetadataMapper.countMany(locationMetadataExample);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findAllStructures(boolean returnGeometry, Long serverVersion, int limit) {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		structureMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		
		List<Location> locations = structureMetadataMapper.selectManyByProperties(structureMetadataExample, null,
		    returnGeometry, 0, limit);
		return convert(locations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countAllStructures(Long serverVersion) {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		return structureMetadataMapper.countMany(structureMetadataExample);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<List<String>, Long> findAllLocationIds(Long serverVersion, int limit) {
		Long lastServerVersion = null;
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		locationMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		
		return getLocationListLongPair(limit, lastServerVersion, locationMetadataExample);
	}
	
	@Override
	public Pair<List<String>, Long> findAllLocationIds(Long serverVersion, int limit, Date fromDate, Date toDate) {
		if (fromDate == null && toDate == null) {
			return findAllLocationIds(serverVersion, limit);
		} else {
			Long lastServerVersion = null;
			LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
			LocationMetadataExample.Criteria criteria = locationMetadataExample.createCriteria();
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
			locationMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
			if (toDate != null && fromDate != null) {
				criteria.andDateCreatedBetween(fromDate, toDate);
			} else if (fromDate != null) {
				criteria.andDateCreatedGreaterThanOrEqualTo(fromDate);
			} else {
				criteria.andDateCreatedLessThanOrEqualTo(toDate);
			}
			return getLocationListLongPair(limit, lastServerVersion, locationMetadataExample);
		}
	}
	
	private Pair<List<String>, Long> getLocationListLongPair(int limit, Long lastServerVersion,
	        LocationMetadataExample locationMetadataExample) {
		int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
		Long serverVersion = lastServerVersion;
		LocationMetadataExample metadataExample = locationMetadataExample;
		List<String> locationIdentifiers = locationMetadataMapper.selectManyIds(metadataExample, 0, fetchLimit);
		
		if (locationIdentifiers != null && !locationIdentifiers.isEmpty()) {
			metadataExample = new LocationMetadataExample();
			metadataExample.createCriteria()
			        .andGeojsonIdEqualTo(locationIdentifiers.get(locationIdentifiers.size() - 1));
			List<LocationMetadata> locationMetadataList = locationMetadataMapper.selectByExample(metadataExample);

			serverVersion = locationMetadataList != null && !locationMetadataList.isEmpty()
			        ? locationMetadataList.get(0).getServerVersion()
			        : 0;
		}
		
		return Pair.of(locationIdentifiers, serverVersion);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LocationDetail> findParentLocationsInclusive(Set<String> identifiers) {
		return locationMetadataMapper.selectLocationHierachy(identifiers, true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LocationDetail> findParentLocationsInclusive(Set<String> identifiers, boolean returnTags) {
		return locationMetadataMapper.selectLocationHierachy(identifiers, returnTags);
	}
	
	@Override
	public PhysicalLocation findLocationByIdentifierAndStatus(String identifier, List<String> status,
	        boolean returnGeometry) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andGeojsonIdEqualTo(identifier).andStatusIn(status);
		locationMetadataExample.setOrderByClause(getOrderByClause(VERSION, DESCENDING));
		
		List<Location> locations = locationMetadataMapper.selectManyWithOptionalGeometry(locationMetadataExample,
		    returnGeometry, 0, 1);
		if (locations == null || locations.isEmpty()) {
			return null;
		}
		
		PhysicalLocation locationEntity = convert(locations.get(0));
		return locationEntity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countStructuresByParentAndServerVersion(String parentIds, long serverVersion) {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria()
		        .andParentIdIn(Arrays.asList(org.apache.commons.lang.StringUtils.split(parentIds, ",")))
		        .andServerVersionGreaterThanOrEqualTo(serverVersion);
		return structureMetadataMapper.countByExample(structureMetadataExample);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countLocationsByServerVersion(long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		return locationMetadataMapper.countByExample(locationMetadataExample);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countLocationsByNames(String locationNames, long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria()
		        .andNameIn(Arrays.asList(org.apache.commons.lang.StringUtils.split(locationNames, ",")))
		        .andServerVersionGreaterThanOrEqualTo(serverVersion)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		return locationMetadataMapper.countByExample(locationMetadataExample);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long countLocationsByIds(List<String> locationIds, long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andGeojsonIdIn(locationIds)
		        .andServerVersionGreaterThanOrEqualTo(serverVersion)
		        .andStatusIn(Arrays.asList(ACTIVE.name(), PENDING_REVIEW.name()));
		return locationMetadataMapper.countByExample(locationMetadataExample);
	}
	
	@Override
	protected Long retrievePrimaryKey(PhysicalLocation entity) {
		Object uniqueId = getUniqueField(entity);
		if (uniqueId == null) {
			return null;
		}
		
		String identifier = uniqueId.toString();
		return retrievePrimaryKey(identifier, entity.isJurisdiction());
	}
	
	@Override
	public Long retrievePrimaryKey(String identifier, boolean isJurisdiction) {
		
		if (isJurisdiction) {
			Location pgEntity = locationMetadataMapper.findById(identifier, true);
			if (pgEntity == null) {
				return null;
			}
			return pgEntity.getId();
		} else {
			Structure pgEntity = structureMetadataMapper.findById(identifier, true);
			if (pgEntity == null) {
				return null;
			}
			return pgEntity.getId();
		}
	}
	
	@Override
	public PhysicalLocation get(String id, boolean returnGeometry, int version) {
		return convert(locationMetadataMapper.findByIdAndVersion(id, true, version));
	}
	
	@Override
	protected Object getUniqueField(PhysicalLocation entity) {
		if (entity == null) {
			return null;
		}
		return entity.getId();
	}
	
	private PhysicalLocation convert(Location entity) {
		if (entity == null || entity.getJson() == null || !(entity.getJson() instanceof PhysicalLocation)) {
			return null;
		}
		
		PhysicalLocation location = (PhysicalLocation) entity.getJson();
		location.setJurisdiction(true);
		return location;
	}
	
	private PhysicalLocation convert(Structure entity) {
		if (entity == null || entity.getJson() == null || !(entity.getJson() instanceof PhysicalLocation)) {
			return null;
		}
		return (PhysicalLocation) entity.getJson();
	}
	
	private List<PhysicalLocation> convert(List<Location> locations) {
		if (locations == null || locations.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<PhysicalLocation> convertedLocations = new ArrayList<>();
		for (Location location : locations) {
			PhysicalLocation convertedLocation = convert(location);
			if (convertedLocation != null) {
				convertedLocations.add(convertedLocation);
			}
		}
		
		return convertedLocations;
	}
	
	private List<PhysicalLocation> convertStructures(List<Structure> structures) {
		if (structures == null || structures.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<PhysicalLocation> convertedStructures = new ArrayList<>();
		for (Structure structure : structures) {
			PhysicalLocation convertedStructure = convert(structure);
			if (convertedStructure != null) {
				convertedStructures.add(convertedStructure);
			}
		}
		
		return convertedStructures;
	}
	
	private Location convert(PhysicalLocation physicalLocation, Long primaryKey) {
		if (physicalLocation == null) {
			return null;
		}
		
		Location pgLocation = new Location();
		pgLocation.setId(primaryKey);
		pgLocation.setJson(physicalLocation);
		
		return pgLocation;
	}
	
	private Structure convertStructure(PhysicalLocation physicalLocation, Long primaryKey) {
		if (physicalLocation == null) {
			return null;
		}
		
		Structure pgStructure = new Structure();
		pgStructure.setId(primaryKey);
		pgStructure.setJson(physicalLocation);
		
		return pgStructure;
	}
	
	private LocationMetadata createMetadata(PhysicalLocation entity, Long id) {
		LocationMetadata locationMetadata = new LocationMetadata();
		locationMetadata.setLocationId(id);
		locationMetadata.setGeojsonId(entity.getId());
		if (entity.getProperties() != null) {
			locationMetadata.setParentId(entity.getProperties().getParentId());
			locationMetadata.setUuid(entity.getProperties().getUid());
			locationMetadata.setType(entity.getProperties().getType());
			locationMetadata.setName(entity.getProperties().getName());
			if (entity.getProperties().getStatus() != null) {
				locationMetadata.setStatus(entity.getProperties().getStatus().name());
			}
			locationMetadata.setVersion(entity.getProperties().getVersion());
		}
		locationMetadata.setServerVersion(entity.getServerVersion());

		if(id != null){
			locationMetadata.setDateEdited(new Date());
		}
		return locationMetadata;
	}
	
	private StructureMetadata createStructureMetadata(PhysicalLocation entity, Long id) {
		StructureMetadata structureMetadata = new StructureMetadata();
		structureMetadata.setStructureId(id);
		structureMetadata.setGeojsonId(entity.getId());
		if (entity.getProperties() != null) {
			structureMetadata.setParentId(entity.getProperties().getParentId());
			structureMetadata.setUuid(entity.getProperties().getUid());
			structureMetadata.setType(entity.getProperties().getType());
			structureMetadata.setName(entity.getProperties().getName());
			if (entity.getProperties().getStatus() != null) {
				structureMetadata.setStatus(entity.getProperties().getStatus().name());
			}
		}
		structureMetadata.setServerVersion(entity.getServerVersion());

		if(id != null){
			structureMetadata.setDateEdited(new Date());
		}
		return structureMetadata;
	}
	
	private void saveLocationTag(PhysicalLocation physicalLocation, Long locationId, boolean isUpdate) {
		Set<LocationTag> locationTagMaps = physicalLocation.getLocationTags();
		
		if (isUpdate) {
			locationTagService.deleteLocationTagMapByLocationId(locationId);
		}
		if (locationTagMaps != null) {
			for (LocationTag locationTag : locationTagMaps) {
				
				LocationTagMap locationTagMap = new LocationTagMap();
				locationTagMap.setLocationId(locationId);
				locationTagMap.setLocationTagId(locationTag.getId());
				locationTagService.addLocationTagMap(locationTagMap);
				
			}
		}
	}
	
	@Override
	public List<PhysicalLocation> searchLocations(LocationSearchBean locationSearchBean) {
		Integer offset = 0;
		if (locationSearchBean.getPageSize() == null || locationSearchBean.getPageSize() == 0) {
			return convert(locationMetadataMapper.selectLocations(locationSearchBean, null, null));
			
		} else if (locationSearchBean.getPageNumber() != null && locationSearchBean.getPageNumber() == 0) {
			throw new IllegalArgumentException("pageNumber should be greater than 0");
			
		} else if (locationSearchBean.getPageNumber() != null) {
			
			offset = locationSearchBean.getPageSize() * (locationSearchBean.getPageNumber() - 1);
		}
		return convert(locationMetadataMapper.selectLocations(locationSearchBean, offset, locationSearchBean.getPageSize()));
	}
	
	@Override
	public int countSearchLocations(LocationSearchBean locationSearchBean) {
		return locationMetadataMapper.selectCountLocations(locationSearchBean);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<LocationDetail> findLocationWithDescendants(String locationId, boolean returnTags) {
		return locationMetadataMapper.selectLocationWithDescendants(locationId, returnTags);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<StructureCount> findStructureCountsForLocation(Set<String> locationIds) {
		return structureMetadataMapper.findStructureCountsForLocation(locationIds);
	}
	
	@Override
	public List<com.ibm.fhir.model.resource.Location> findJurisdictionsById(String id) {
		return convertToFHIRLocation(Collections.singletonList(get(id, false)));
	}
	
	@Override
	public List<com.ibm.fhir.model.resource.Location> findLocationsById(String id) {
		return convertToFHIRLocation(Collections.singletonList(getStructure(id, false)));
	}
	
	@Override
	public List<com.ibm.fhir.model.resource.Location> findLocationByJurisdiction(String jurisdiction) {
		return convertToFHIRLocation(findStructuresByProperties(false, jurisdiction, null, Integer.MAX_VALUE));
	}
	
	@Override
	public List<String> findChildLocationByJurisdiction(String id) {
		return locationMetadataMapper.selectChildrenIds(id);
	}
	
	private List<com.ibm.fhir.model.resource.Location> convertToFHIRLocation(List<PhysicalLocation> locations) {
		return locations.stream().map(location -> LocationConverter.convertPhysicalLocationToLocationResource(location))
		        .collect(Collectors.toList());
	}
	
	@Override
	protected String getSequenceName() {
		return LOCATION_SEQUENCE;
	}
	
	@Override
	public long getStructureNextServerVersion() {
		return getNextServerVersion(STRUCTURE_SEQUENCE);
	}
}
