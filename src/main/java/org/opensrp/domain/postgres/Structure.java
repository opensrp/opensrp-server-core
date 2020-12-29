package org.opensrp.domain.postgres;

public class Structure {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.structure.id
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.structure.json
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	private Object json;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.structure.geometry
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	private Object geometry;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.structure.id
	 * @return  the value of core.structure.id
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.structure.id
	 * @param id  the value for core.structure.id
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.structure.json
	 * @return  the value of core.structure.json
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	public Object getJson() {
		return json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.structure.json
	 * @param json  the value for core.structure.json
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	public void setJson(Object json) {
		this.json = json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.structure.geometry
	 * @return  the value of core.structure.geometry
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	public Object getGeometry() {
		return geometry;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.structure.geometry
	 * @param geometry  the value for core.structure.geometry
	 * @mbg.generated  Mon Dec 28 19:35:08 PKT 2020
	 */
	public void setGeometry(Object geometry) {
		this.geometry = geometry;
	}
}