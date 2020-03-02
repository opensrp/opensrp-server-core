/**
 * 
 */
package org.opensrp.domain;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Samuel Githengi created on 02/19/20
 */
public class BaseDataEntity extends CouchDbDocument {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private String type;
	
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
