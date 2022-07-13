package org.opensrp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportImagesSummary {

    private List<ExportFlagProblemEventImageMetadata> exportFlagProblemEventImageMetadataList;
    private Set<String> servicePoints;
}
