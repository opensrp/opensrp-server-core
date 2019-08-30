/**
 * 
 */
package org.opensrp.domain;

import java.io.Serializable;

/**
 * @author Samuel Githengi created on 08/30/19
 */
public class CodeSystem implements Serializable {

	private static final long serialVersionUID = -2587903025581183298L;

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
