package org.opensrp.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StructureCount implements Serializable {

	private static final long serialVersionUID = -6553112674200592573L;

	private String parentId;

	private int count;
}
