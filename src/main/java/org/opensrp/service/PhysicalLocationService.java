package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.opensrp.domain.PhysicalLocation;
import org.opensrp.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhysicalLocationService {

	private static Logger logger = LoggerFactory.getLogger(PhysicalLocationService.class.toString());

	private LocationRepository locationRepository;

	@Autowired
	public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	public PhysicalLocation getLocation(String id) {
		return locationRepository.get(id);
	}

	public PhysicalLocation getStructure(String id) {
		return locationRepository.getStructure(id);
	}

	public List<PhysicalLocation> getAllLocations() {
		return locationRepository.getAll();
	}

	public void addOrUpdate(PhysicalLocation physicalLocation) {
		if (StringUtils.isBlank(physicalLocation.getId()))
			throw new IllegalArgumentException("id not specified");
		if ((physicalLocation.isJurisdiction() && getLocation(physicalLocation.getId()) == null)
				|| (!physicalLocation.isJurisdiction() && getStructure(physicalLocation.getId()) == null)) {
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
		locationRepository.update(physicalLocation);
	}

	public List<PhysicalLocation> findLocationsByServerVersion(long serverVersion) {
		return locationRepository.findLocationsByServerVersion(serverVersion);
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
		} catch (Exception e) {
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
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void saveLocations(List<PhysicalLocation> locations, boolean isJurisdiction) {
		for (PhysicalLocation location : locations) {
			location.setJurisdiction(isJurisdiction);
			addOrUpdate(location);
		}

	}

}