package org.opensrp.domain;

import com.google.gson.annotations.SerializedName;

public class Geometry {

	enum GeometryType {
		@SerializedName("Point")
		POINT,
		@SerializedName("Polygon")
		POLYGON,
		@SerializedName("MultiPolygon")
		MULITI_POLYGON
	};

	private GeometryType type;

	private double[][][] coordinates;

	public GeometryType getType() {
		return type;
	}

	public void setType(GeometryType type) {
		this.type = type;
	}

	public double[][][] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double[][][] coordinates) {
		this.coordinates = coordinates;
	}

}
