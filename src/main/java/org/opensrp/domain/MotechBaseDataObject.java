/**
 * 
 */
package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Samuel Githengi created on 02/19/20
 */
public class MotechBaseDataObject extends org.motechproject.model.MotechBaseDataObject {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	protected String type;
	
	protected MotechBaseDataObject() {
		super();
		this.type = this.getClass().getSimpleName();
	}
	
	protected MotechBaseDataObject(String type) {
		super();
		this.type = type;
	}
	
}
