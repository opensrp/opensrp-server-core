package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskSearchBean extends BaseSearchBean{

	@NotBlank
	private String planIdentifier;
	private List<String> groupIdentifiers;
	private String code;
	private String status;
	private String businessStatus;
	private boolean returnTaskCountOnly;
}
