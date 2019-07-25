package org.opensrp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.StructureDetails;

public interface LocationRepository extends BaseRepository<PhysicalLocation> {

	PhysicalLocation getStructure(String id);

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
}
