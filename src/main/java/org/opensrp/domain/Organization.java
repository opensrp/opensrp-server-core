/**
 *
 */
package org.opensrp.domain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Samuel Githengi created on 08/30/19
 */
public class Organization implements Serializable {

	private static final long serialVersionUID = -9204925528493297488L;

	private Long id;

	private String identifier;

	private boolean active;

	private String name;

	private Long partOf;

	public CodeSystem type;

	private Set<AssignedLocations> assignedLocations;

	private DateTime dateCreated;

	private DateTime dateEdited;

	private long serverVersion;
	private Integer memberCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Long getPartOf() {
		return partOf;
	}

	public void setPartOf(Long partOf) {
		this.partOf = partOf;
	}

	public CodeSystem getType() {
		return type;
	}

	public void setType(CodeSystem type) {
		this.type = type;
	}

	public Set<AssignedLocations> getAssignedLocations() {
		return assignedLocations;
	}

	public void setAssignedLocations(Set<AssignedLocations> assignedLocations) {
		this.assignedLocations = assignedLocations;
	}

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getDateEdited() {
		return dateEdited;
	}

	public void setDateEdited(DateTime dateEdited) {
		this.dateEdited = dateEdited;
	}

	public long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(long serverVersion) {
		this.serverVersion = serverVersion;
	}
}
