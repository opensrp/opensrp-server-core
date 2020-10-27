package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockObjectMetadata {

	private Long date_created;
	private String to_from;
	private Long date_updated;
	private long version;

}
