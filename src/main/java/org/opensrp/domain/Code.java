/**
 * 
 */
package org.opensrp.domain;

import java.io.Serializable;

/**
 * @author Samuel Githengi created on 09/12/19
 */
class Code implements Serializable {

	private static final long serialVersionUID = 4580804190266539302L;

	private String system;

	private String code;

	private String display;

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}