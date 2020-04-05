package org.opensrp.domain;

import java.util.Set;

public class PhysicalLocation {
	
	private String type;
	
	private String id;
	
	private Geometry geometry;
	
	private LocationProperty properties;
	
	private Long serverVersion;
	
	private Set<LocationTag> locationTags;
	
	private transient boolean isJurisdiction;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public LocationProperty getProperties() {
		return properties;
	}
	
	public void setProperties(LocationProperty properties) {
		this.properties = properties;
	}
	
	public Long getServerVersion() {
		return serverVersion;
	}
	
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
	
	public boolean isJurisdiction() {
		return isJurisdiction;
	}
	
	public void setJurisdiction(boolean isJurisdiction) {
		this.isJurisdiction = isJurisdiction;
	}
	
	public Set<LocationTag> getLocationTags() {
		return locationTags;
	}
	
	public void setLocationTags(Set<LocationTag> locationTags) {
		this.locationTags = locationTags;
	}
	
	@Override
	public String toString() {
		return "PhysicalLocation [type=" + type + ", id=" + id + ", geometry=" + geometry + ", properties=" + properties
		        + ", serverVersion=" + serverVersion + ", locationTags=" + locationTags + "]";
	}
	
}
