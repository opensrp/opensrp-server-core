package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
public class PractitionerSearchBean extends BaseSearchBean {

	private Long serverVersion;

	public Long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
}
