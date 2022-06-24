package org.opensrp.domain;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"identifier"})
public class LocationDetail implements Serializable {

    private static final long serialVersionUID = 7360003982578282029L;

    private Long id;

    private String identifier;

    private String name;

    private String parentId;

    private boolean voided;

    private String tags;

    private Integer geographicLevel;

}
