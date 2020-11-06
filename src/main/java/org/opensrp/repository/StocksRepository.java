package org.opensrp.repository;

import java.util.List;

import org.smartregister.domain.Stock;
import org.opensrp.search.StockSearchBean;

public interface StocksRepository extends BaseRepository<Stock>, StockDao {
	
	List<Stock> findAllByProviderid(String providerid);
	
	public List<Stock> findAllByIdentifier(String vaccine_type, String vaccine_type_id);
	
	public Stock findById(String id);
	
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int limit);
	
	public List<Stock> findStocks(StockSearchBean searchBean);
	
	public List<Stock> findAllStocks();

	public Stock findByIdentifierAndServicePointId(String identifier, String locationId);

	Stock getById(Long id);

	void delete(Long stockId);

	public List<Stock> findStocksByLocationId(String locationId);
	
}
