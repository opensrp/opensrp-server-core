package org.opensrp.domain;

import java.io.Serializable;

public class LocationTag implements Serializable {
	
	private static final long serialVersionUID = -8367551045898354954L;
	
	private Long id;
	
	private Boolean active;
	
	private String name;
	
	private String description;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
