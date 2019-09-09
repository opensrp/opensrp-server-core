/**
 * 
 */
package org.opensrp.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author Samuel Githengi created on 08/30/19
 */
public class Organization implements Serializable {

	private static final long serialVersionUID = -9204925528493297488L;

	private String identifier;

	private boolean active;

	private String name;

	private String partOf;

	public List<CodeSystem> type;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPartOf() {
		return partOf;
	}

	public void setPartOf(String partOf) {
		this.partOf = partOf;
	}

	public List<CodeSystem> getType() {
		return type;
	}

	public void setType(List<CodeSystem> type) {
		this.type = type;
	}

}
