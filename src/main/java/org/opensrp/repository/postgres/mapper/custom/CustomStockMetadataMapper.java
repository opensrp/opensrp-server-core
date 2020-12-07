package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.PgStockAndProductDetails;
import org.opensrp.domain.postgres.Stock;
import org.opensrp.domain.postgres.StockMetadata;
import org.opensrp.domain.postgres.StockMetadataExample;
import org.opensrp.repository.postgres.mapper.StockMetadataMapper;
import org.opensrp.search.StockSearchBean;

public interface CustomStockMetadataMapper extends StockMetadataMapper {
	
	Stock selectByDocumentId(String documentId);
	
	List<Stock> selectMany(@Param("example") StockMetadataExample stockMetadataExample, @Param("offset") int offset,
	        @Param("limit") int limit);
	
	List<Stock> selectByIdentifier(@Param("stockTypeId") String stockTypeId, @Param("offset") int offset,
	        @Param("limit") int limit);
	
	List<Stock> selectManyBySearchBean(@Param("searchBean") StockSearchBean searchBean,
	        @Param("todayDate") Date todayDate, @Param("orderByClause") String orderByClause, @Param("offset") int offset, @Param("limit") int limit);

	StockMetadata selectByStockId(Long stockId);

	List<PgStockAndProductDetails> selectManyStockAndProductDetailsByServicePointId(@Param("locations") List<String> locations);
}
