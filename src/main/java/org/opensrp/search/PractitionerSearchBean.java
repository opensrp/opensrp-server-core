package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PractitionerSearchBean {

	private Long serverVersion;

	public enum OrderByType {
		ASC, DESC
	}

	public enum FieldName {
		id, server_version
	}

	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private OrderByType orderByType;

	private FieldName orderByFieldName;
}
