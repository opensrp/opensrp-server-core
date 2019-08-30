package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.StructureDetails;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.LocationMetadata;
import org.opensrp.domain.postgres.LocationMetadataExample;
import org.opensrp.domain.postgres.Structure;
import org.opensrp.domain.postgres.StructureFamilyDetails;
import org.opensrp.domain.postgres.StructureMetadata;
import org.opensrp.domain.postgres.StructureMetadataExample;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationMetadataMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStructureMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStructureMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LocationRepositoryImpl extends BaseRepositoryImpl<PhysicalLocation> implements LocationRepository {

	@Autowired
	private CustomLocationMapper locationMapper;

	@Autowired
	private CustomLocationMetadataMapper locationMetadataMapper;

	@Autowired
	private CustomStructureMapper structureMapper;

	@Autowired
	private CustomStructureMetadataMapper structureMetadataMapper;

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

	private void addLocation(PhysicalLocation entity) {

		Location pgLocation = convert(entity, null);
		if (pgLocation == null) {
			return;
		}

		int rowsAffected = locationMapper.insertSelectiveAndSetId(pgLocation);
		if (rowsAffected < 1 || pgLocation.getId() == null) {
			return;
		}

		LocationMetadata locationMetadata = createMetadata(entity, pgLocation.getId());

		locationMetadataMapper.insertSelective(locationMetadata);

	}

	private void addStructure(PhysicalLocation entity) {

		Structure pgStructure = convertStructure(entity, null);
		if (pgStructure == null) {
			return;
		}

		int rowsAffected = structureMapper.insertSelectiveAndSetId(pgStructure);
		if (rowsAffected < 1 || pgStructure.getId() == null) {
			return;
		}

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
		LocationMetadata locationMetadata = createMetadata(entity, pgLocation.getId());

		int rowsAffected = locationMapper.updateByPrimaryKey(pgLocation);
		if (rowsAffected < 1) {
			return;
		}

		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria().andLocationIdEqualTo(id);
		locationMetadata.setId(locationMetadataMapper.selectByExample(locationMetadataExample).get(0).getId());
		locationMetadataMapper.updateByPrimaryKey(locationMetadata);
	}

	private void updateStructure(PhysicalLocation entity, Long id) {
		Structure pgStructure = convertStructure(entity, id);
		if (pgStructure == null) {
			return;
		}
		StructureMetadata structureMetadata = createStructureMetadata(entity, pgStructure.getId());

		int rowsAffected = structureMapper.updateByPrimaryKey(pgStructure);
		if (rowsAffected < 1) {
			return;
		}

		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andStructureIdEqualTo(id);
		structureMetadata.setId(structureMetadataMapper.selectByExample(structureMetadataExample).get(0).getId());
		structureMetadataMapper.updateByPrimaryKey(structureMetadata);
	}

	@Override
	public List<PhysicalLocation> getAll() {
		List<Location> locations = locationMetadataMapper.selectMany(new LocationMetadataExample(), 0,
				DEFAULT_FETCH_SIZE);
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
		locationMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		locationMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		List<Location> locations = locationMetadataMapper.selectMany(locationMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}

	@Override
	public List<PhysicalLocation> findLocationsByNames(String locationNames, long serverVersion) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		locationMetadataExample.createCriteria()
				.andNameIn(Arrays.asList(org.apache.commons.lang.StringUtils.split(locationNames, ",")))
				.andServerVersionGreaterThanOrEqualTo(serverVersion);
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
	public Collection<StructureDetails> findStructureAndFamilyDetails(double latitude, double longitude,
			double radius) {
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
			Map<String, String> properties) {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		if (StringUtils.isNotBlank(parentId)) {
			structureMetadataExample.createCriteria().andParentIdEqualTo(parentId);
		}
		List<Location> locations = structureMetadataMapper.selectManyByProperties(structureMetadataExample, properties,
				returnGeometry, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationsByIds(boolean returnGeometry, List<String> ids) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		if(ids == null || ids.isEmpty()) {
			return null;
		}

		locationMetadataExample.createCriteria().andGeojsonIdIn(ids);

		List<Location> locations = locationMetadataMapper.selectManyById(locationMetadataExample,
				returnGeometry, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhysicalLocation> findLocationByIdWithChildren(boolean returnGeometry, String id) {
		LocationMetadataExample locationMetadataExample = new LocationMetadataExample();
		if(id == null) {
			return null;
		}
		List<Location> locations = locationMetadataMapper.selectWithChildren(locationMetadataExample,
				returnGeometry, id, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}

	@Override
	protected Long retrievePrimaryKey(PhysicalLocation entity) {
		Object uniqueId = getUniqueField(entity);
		if (uniqueId == null) {
			return null;
		}

		String identifier = uniqueId.toString();

		if (entity.isJurisdiction()) {
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
		}
		locationMetadata.setServerVersion(entity.getServerVersion());
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
		return structureMetadata;
	}

}
