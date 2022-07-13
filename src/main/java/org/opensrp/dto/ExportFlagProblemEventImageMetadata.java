package org.opensrp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportFlagProblemEventImageMetadata {

    private String stockId;
    private String servicePointName;
    private String productName;
}
