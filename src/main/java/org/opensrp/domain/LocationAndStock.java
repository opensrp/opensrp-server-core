package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opensrp.domain.postgres.Location;
import org.opensrp.domain.postgres.Stock;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationAndStock extends Location {

    private List<Stock> stocks;

}
