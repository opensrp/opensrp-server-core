package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.domain.Stock;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhysicalLocationAndStock extends PhysicalLocation {

	private List<Stock> stocks;

}
