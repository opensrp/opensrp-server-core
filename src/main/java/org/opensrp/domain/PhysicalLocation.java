package org.opensrp.domain;

public class PhysicalLocation {

	private String type;

	private long id;

	private Geometry geometry;

	private LocationProperty properties;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

}
