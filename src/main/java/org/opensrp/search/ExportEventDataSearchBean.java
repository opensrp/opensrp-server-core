package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportEventDataSearchBean {

	@NotBlank
	private List<String> eventTypes;

	@NotBlank
	private String planIdentifier;

	private String fromDate;

	private String toDate;
}
