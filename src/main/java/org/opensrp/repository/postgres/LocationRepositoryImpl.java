package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.LocationMetadata;
import org.opensrp.domain.postgres.LocationMetadataExample;
import org.opensrp.domain.postgres.Structure;
import org.opensrp.domain.postgres.StructureMetadata;
import org.opensrp.domain.postgres.StructureMetadataExample;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationMetadataMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStructureMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStructureMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
		return convert(locationMetadataMapper.findById(id));
	}

	@Override
	public PhysicalLocation getStructure(String id) {
		return convert(structureMetadataMapper.findById(id));
	}

	@Override
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

		org.opensrp.domain.postgres.Location pgLocation = convert(entity, null);
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

		org.opensrp.domain.postgres.Structure pgStructure = convertStructure(entity, null);
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
		org.opensrp.domain.postgres.Location pgLocation = convert(entity, id);
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
		org.opensrp.domain.postgres.Structure pgStructure = convertStructure(entity, id);
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
		List<org.opensrp.domain.postgres.Location> locations = locationMetadataMapper
				.selectMany(new LocationMetadataExample(), 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}

	@Override
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
		List<org.opensrp.domain.postgres.Location> locations = locationMetadataMapper
				.selectMany(locationMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convert(locations);
	}

	@Override
	public List<PhysicalLocation> findStructuresByParentAndServerVersion(String parentId, long serverVersion) {
		StructureMetadataExample structureMetadataExample = new StructureMetadataExample();
		structureMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		List<org.opensrp.domain.postgres.Structure> locations = structureMetadataMapper
				.selectMany(structureMetadataExample, 0, DEFAULT_FETCH_SIZE);
		return convertStructures(locations);
	}

	@Override
	protected Long retrievePrimaryKey(PhysicalLocation entity) {
		Object uniqueId = getUniqueField(entity);
		if (uniqueId == null) {
			return null;
		}

		String identifier = uniqueId.toString();

		if (entity.isJurisdiction()) {
			org.opensrp.domain.postgres.Location pgEntity = locationMetadataMapper.findById(identifier);
			if (pgEntity == null) {
				return null;
			}
			return pgEntity.getId();
		} else {
			org.opensrp.domain.postgres.Structure pgEntity = structureMetadataMapper.findById(identifier);
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
		return entity.getProperties().getUid();
	}

	private PhysicalLocation convert(Location entity) {
		if (entity == null || entity.getJson() == null || !(entity.getJson() instanceof PhysicalLocation)) {
			return null;
		}
		return (PhysicalLocation) entity.getJson();
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
		for (org.opensrp.domain.postgres.Location location : locations) {
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
		for (org.opensrp.domain.postgres.Structure structure : structures) {
			PhysicalLocation convertedStructure = convert(structure);
			if (convertedStructure != null) {
				convertedStructures.add(convertedStructure);
			}
		}

		return convertedStructures;
	}

	private org.opensrp.domain.postgres.Location convert(PhysicalLocation task, Long primaryKey) {
		if (task == null) {
			return null;
		}

		org.opensrp.domain.postgres.Location pgLocation = new org.opensrp.domain.postgres.Location();
		pgLocation.setId(primaryKey);
		pgLocation.setJson(task);

		return pgLocation;
	}

	private org.opensrp.domain.postgres.Structure convertStructure(PhysicalLocation task, Long primaryKey) {
		if (task == null) {
			return null;
		}

		org.opensrp.domain.postgres.Structure pgStructure = new org.opensrp.domain.postgres.Structure();
		pgStructure.setId(primaryKey);
		pgStructure.setJson(task);

		return pgStructure;
	}

	private LocationMetadata createMetadata(PhysicalLocation entity, Long id) {
		LocationMetadata locationMetadata = new LocationMetadata();
		locationMetadata.setLocationId(id);
		locationMetadata.setGeojsonId(entity.getId());
		locationMetadata.setParentId(entity.getProperties().getParentId());
		locationMetadata.setType(entity.getProperties().getType());
		locationMetadata.setStatus(entity.getProperties().getStatus().name());
		locationMetadata.setServerVersion(entity.getServerVersion());
		return locationMetadata;
	}

	private StructureMetadata createStructureMetadata(PhysicalLocation entity, Long id) {
		StructureMetadata structureMetadata = new StructureMetadata();
		structureMetadata.setStructureId(id);
		structureMetadata.setGeojsonId(entity.getId());
		structureMetadata.setParentId(entity.getProperties().getParentId());
		structureMetadata.setType(entity.getProperties().getType());
		structureMetadata.setStatus(entity.getProperties().getStatus().name());
		structureMetadata.setServerVersion(entity.getServerVersion());
		return structureMetadata;
	}

}
