package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Stock;
import org.opensrp.repository.postgres.mapper.StockMapper;
import org.springframework.lang.NonNull;

public interface CustomStockMapper extends StockMapper {
	
	int insertSelectiveAndSetId(Stock stock);

	Stock selectByIdentifierAndLocationId(@Param("identifier") @NonNull String identifier,
			@Param("locationId") @NonNull String locationId);
	
	Long selectServerVersionByPrimaryKey(Long id);
	
	int updateByPrimaryKeyAndGenerateServerVersion(Stock record);

}
