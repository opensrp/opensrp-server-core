package org.opensrp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportImagesSummary {

	List<ExportFlagProblemEventImageMetadata>  exportFlagProblemEventImageMetadataList;
	List<String> servicePointIds;
}
