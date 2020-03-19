/**
 * 
 */
package org.opensrp.domain;

import java.io.Serializable;
import java.util.List;

import org.opensrp.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Samuel Githengi created on 02/19/20
 */
@JsonInclude(Include.NON_NULL)
public class BaseDataEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private String type;
	
	public static final String ATTACHMENTS_NAME = "_attachments";
	
	private String id;
	
	private String revision;
	
	private List<String> conflicts;
	
	@JsonProperty("_id")
	public String getId() {
		return id;
	}
	
	@JsonProperty("_id")
	public void setId(String s) {
		Assert.hasText(s, "id must have a value");
		if (id != null && id.equals(s)) {
			return;
		}
		if (id != null) {
			throw new IllegalStateException("cannot set id, id already set");
		}
		id = s;
	}
	
	@JsonProperty("_rev")
	public String getRevision() {
		return revision;
	}
	
	@JsonProperty("_rev")
	public void setRevision(String s) {
		// no empty strings thanks
		if (s != null && s.length() == 0) {
			return;
		}
		this.revision = s;
	}
	
	@JsonIgnore
	public boolean isNew() {
		return revision == null;
	}

	
	@JsonProperty("_conflicts")
	void setConflicts(List<String> conflicts) {
		this.conflicts = conflicts;
	}
	
	/**
	 * @return a list of conflicting revisions. Note: Will only be populated if this document has
	 *         been loaded through the CouchDbConnector.getWithConflicts method.
	 */
	@JsonIgnore
	public List<String> getConflicts() {
		return conflicts;
	}
	
	/**
	 * @return true if this document has a conflict. Note: Will only give a correct value if this
	 *         document has been loaded through the CouchDbConnector.getWithConflicts method.
	 */
	public boolean hasConflict() {
		return conflicts != null && !conflicts.isEmpty();
	}
	
	
	protected BaseDataEntity() {
		super();
		setType(this.getClass().getSimpleName());
	}
	
	protected BaseDataEntity(String type) {
		super();
		setType(type);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
