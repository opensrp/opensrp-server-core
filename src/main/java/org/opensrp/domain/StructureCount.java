package org.opensrp.domain;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StructureCount implements Serializable {

    public static final String STRUCTURE_COUNT = "structureCount";
    private static final long serialVersionUID = -6553112674200592573L;
    private String parentId;

    private int count;
}
