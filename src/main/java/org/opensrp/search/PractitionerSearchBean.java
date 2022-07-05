package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PractitionerSearchBean extends BaseSearchBean {

	private Long serverVersion;
}
