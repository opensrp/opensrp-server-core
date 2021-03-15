package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;

import com.ibm.fhir.model.resource.Bundle;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.opensrp.common.AllConstants.BaseEntity;
import org.smartregister.domain.StockAndProductDetails;
import org.smartregister.domain.Stock;
import org.opensrp.repository.StocksRepository;
import org.opensrp.search.StockSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class StocksRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("stocksRepositoryPostgres")
	private StocksRepository stocksRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("location.sql");
		scripts.add("structure.sql");
		scripts.add("location_tag.sql");
		scripts.add("stock.sql");
		scripts.add("product_catalogue.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Stock stock = stocksRepository.get("05934ae338431f28bf6793b241978ad9");
		assertEquals("5", stock.getIdentifier());
		assertEquals(20, stock.getValue());
		assertEquals("1", stock.getVaccineTypeId());
		assertEquals(1521009418783l, stock.getServerVersion().longValue());
		assertNull(stocksRepository.get("07271855-4018-497a-b180-6af"));
	}
	
	@Test
	public void testAdd() {
		Stock stock = new Stock("521", "VC1", "received", "tester1", 31, 1521499800000l, "VCC1", 1521536143239l,
		        1521536179443l);
		stocksRepository.add(stock);
		assertEquals(16, stocksRepository.getAll().size());
		
		List<Stock> stocks = stocksRepository.findAllByIdentifier("vaccine_type", "VC1");
		
		assertEquals(1, stocks.size());
		assertEquals("tester1", stocks.get(0).getProviderid());
		assertEquals(31, stock.getValue());
		assertEquals("VC1", stock.getVaccineTypeId());
		MatcherAssert.assertThat(stocks, Matchers.contains(Matchers.hasProperty("serverVersion",Matchers.greaterThan(0l))));
	}
	
	@Test
	public void testUpdate() {
		Stock stock = stocksRepository.get("05934ae338431f28bf6793b241b2daa6");
		long now = System.currentTimeMillis();
		stock.setDateUpdated(now);
		stock.setValue(23);
		long serverVersion=stock.getServerVersion();
		stocksRepository.update(stock);
		
		Stock updatedStock = stocksRepository.get("05934ae338431f28bf6793b241b2daa6");
		assertEquals(now, updatedStock.getDateUpdated().longValue());
		assertEquals(23, stock.getValue());
		MatcherAssert.assertThat(updatedStock.getServerVersion(), Matchers.greaterThan(serverVersion));
		
	}
	
	@Test
	public void testGetAll() {
		assertEquals(15, stocksRepository.getAll().size());
		stocksRepository.safeRemove(stocksRepository.get("05934ae338431f28bf6793b241b2daa6"));
		List<Stock> stocks = stocksRepository.getAll();
		assertEquals(14, stocks.size());
		for (Stock stock : stocks)
			assertNotEquals("05934ae338431f28bf6793b241b2daa6", stock.getId());
		
	}
	
	@Test
	public void testSafeRemove() {
		stocksRepository.safeRemove(stocksRepository.get("05934ae338431f28bf6793b2419a606f"));
		List<Stock> stocks = stocksRepository.getAll();
		assertEquals(14, stocks.size());
		for (Stock stock : stocks)
			assertNotEquals("05934ae338431f28bf6793b2419a606f", stock.getId());
		
		assertNull(stocksRepository.get("05934ae338431f28bf6793b2419a606f"));
	}
	
	@Test
	public void testFindAllByProviderid() {
		assertEquals(12, stocksRepository.findAllByProviderid("biddemo").size());
		
		List<Stock> stocks = stocksRepository.findAllByProviderid("biddemo1");
		assertEquals(3, stocks.size());
		for (Stock stock : stocks)
			assertEquals("biddemo1", stock.getProviderid());
		
		assertTrue(stocksRepository.findAllByProviderid("biddemo2").isEmpty());
	}
	
	@Test
	public void testFindAllByIdentifier() {
		assertEquals(11, stocksRepository.findAllByIdentifier("vaccine_type", "1").size());
		
		List<Stock> stocks = stocksRepository.findAllByIdentifier("vaccine_type", "2");
		assertEquals(4, stocks.size());
		for (Stock stock : stocks)
			assertEquals("2", stock.getVaccineTypeId());
		
		assertTrue(stocksRepository.findAllByIdentifier("vaccine_type", "19").isEmpty());
	}
	
	@Test
	public void testFindById() {
		Stock stock = stocksRepository.findById("05934ae338431f28bf6793b241b2df09");
		assertEquals("12", stock.getIdentifier());
		assertEquals(-2, stock.getValue());
		assertEquals("1", stock.getVaccineTypeId());
		assertEquals("Physical_recount", stock.getToFrom());
		assertEquals("loss_adjustment", stock.getTransactionType());
		assertNull(stocksRepository.findById("07271855-4018-497a-b180-6af"));
	}
	
	@Test
	public void testFindStocksWithOrder() {
		StockSearchBean searchBean = new StockSearchBean();
		List<String> locations = new ArrayList<>();
		searchBean.setStockTypeId("1");
		searchBean.setLocations(locations);
		List<Stock> stocks = stocksRepository.findStocks(searchBean, BaseEntity.SERVER_VERSIOIN, "asc",0, 5);
		assertEquals(5, stocks.size());
		long previousVersion = 0;
		for (Stock stock : stocks) {
			assertEquals("1", stock.getVaccineTypeId());
			assertTrue(stock.getServerVersion() >= previousVersion);
			previousVersion = stock.getServerVersion();
		}
		
	}
	
	@Test
	public void testFindStocksStockbeanOnly() {
		StockSearchBean searchBean = new StockSearchBean();
		
		searchBean.setIdentifier("10");
		List<Stock> stocks = stocksRepository.findStocks(searchBean);
		assertEquals(1, stocks.size());
		
		assertEquals(-19, stocks.get(0).getValue());
		assertEquals("2", stocks.get(0).getVaccineTypeId());
		assertEquals("C/C", stocks.get(0).getToFrom());
		assertEquals("issued", stocks.get(0).getTransactionType());
		
		searchBean = new StockSearchBean();
		searchBean.setStockTypeId("1");
		assertEquals(11, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setTransactionType("issued");
		searchBean.setProviderId("biddemo");
		assertEquals(3, stocksRepository.findStocks(searchBean).size());
		
		searchBean = new StockSearchBean();
		searchBean.setValue("10");
		assertEquals(2, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setValue("2");
		stocks = stocksRepository.findStocks(searchBean);
		assertEquals(1, stocks.size());
		assertEquals("14", stocks.get(0).getIdentifier());
		assertEquals("1", stocks.get(0).getVaccineTypeId());
		assertEquals("DHO", stocks.get(0).getToFrom());
		assertEquals("received", stocks.get(0).getTransactionType());
		
		searchBean = new StockSearchBean();
		searchBean.setDateCreated("1518559200000");
		assertEquals(4, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setToFrom("DHO");
		assertEquals(2, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setDateUpdated("1521007053945");
		stocks = stocksRepository.findStocks(searchBean);
		assertEquals(1, stocks.size());
		assertEquals("2", stocks.get(0).getIdentifier());
		assertEquals("1", stocks.get(0).getVaccineTypeId());
		assertEquals("DHO", stocks.get(0).getToFrom());
		assertEquals("received", stocks.get(0).getTransactionType());
		
		searchBean = new StockSearchBean();
		searchBean.setServerVersion(1521009418783l);
		assertEquals(11, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setServerVersion(1521023046990l);
		assertEquals(8, stocksRepository.findStocks(searchBean).size());
		
	}
	
	@Test
	public void testFindAllStocks() {
		assertEquals(15, stocksRepository.findAllStocks().size());
		stocksRepository.safeRemove(stocksRepository.get("05934ae338431f28bf6793b241b2df09"));
		List<Stock> stocks = stocksRepository.findAllStocks();
		assertEquals(14, stocks.size());
		for (Stock stock : stocks)
			assertNotEquals("05934ae338431f28bf6793b241b2df09", stock.getId());
	}

	@Test
	public void testFindInventoryItemsInAJurisdiction() {
        Stock stock = createInventoryStockObject("90397");
        stocksRepository.add(stock);
		List<Bundle> bundles = stocksRepository.findInventoryItemsInAJurisdiction("3734");
		assertEquals(1,bundles.size());
		assertEquals(2, bundles.get(0).getEntry().size());
	}

	@Test
	public void testFindInventoryItemsInAJurisdictionWithNoServicePoint() {
		Stock stock = createInventoryStockObject("90397");
		stocksRepository.add(stock);
		List<Bundle> bundles = stocksRepository.findInventoryItemsInAJurisdiction("3730");
		assertEquals(0,bundles.size());
	}

	@Test
	public void testFindInventoryInAServicePoint() {
		Stock stock = createInventoryStockObject("90397");
		stocksRepository.add(stock);
		List<Bundle> bundles = stocksRepository.findInventoryInAServicePoint("90397");
		assertEquals(1,bundles.size());
		assertEquals(2, bundles.get(0).getEntry().size());
	}

	@Test
	public void testGetInventoryWithProductDetails() {
		Stock stock = createInventoryStockObject("3734");
		stocksRepository.add(stock);
		List<String> locations = new ArrayList<>();
		locations.add("3734");
		List<StockAndProductDetails> inventoryItems = stocksRepository.getInventoryWithProductDetails(locations);
		assertEquals(1,inventoryItems.size());
	}

	@Test
	public void testGetInventoryWithProductDetailsByStockId() {
		Stock stock = createInventoryStockObject("3734");
		stocksRepository.add(stock);
		List<String> locations = new ArrayList<>();
		locations.add("3734");
		StockSearchBean stockSearchBean = new StockSearchBean();
		stockSearchBean.setLocations(locations);
		List<Stock> stocks = stocksRepository.findStocksByLocationId(stockSearchBean);
		List<StockAndProductDetails> inventoryItems = stocksRepository.getInventoryWithProductDetailsByStockId(stocks.get(0).getId());
		assertEquals(1,inventoryItems.size());
	}

	@Test
	public void testFindStocksWithProductDetails() {
		Stock stock = createInventoryStockObject("3734");
		stocksRepository.add(stock);
		StockSearchBean searchBean = new StockSearchBean();
		searchBean.setReturnProduct(true);
		List<String> locations = new ArrayList<>();
		locations.add("3734");
		searchBean.setLocations(locations);
		List<Stock> stocks = stocksRepository.findStocks(searchBean, BaseEntity.SERVER_VERSIOIN, "asc",0, 5);
		assertEquals(1, stocks.size());
		assertEquals("1",stocks.get(0).getIdentifier());
		assertNotNull(stocks.get(0).getProduct());
		assertEquals(new Long(1), stocks.get(0).getProduct().getUniqueId());
		assertEquals("Midwifery Kit", stocks.get(0).getProduct().getProductName());

	}


	@Test
	public void testGetStockById() {
		Stock stock = createInventoryStockObject("3734");
		stocksRepository.add(stock);
		List<String> locations = new ArrayList<>();
		locations.add("3734");
		StockSearchBean stockSearchBean = new StockSearchBean();
		stockSearchBean.setLocations(locations);
		List<Stock> stocks = stocksRepository.findStocksByLocationId(stockSearchBean);
		List<Bundle> bundles = stocksRepository.getStockById(stocks.get(0).getId());
		assertEquals(1,bundles.size());
		assertEquals(2, bundles.get(0).getEntry().size());
	}

	private Stock createInventoryStockObject(String locationId) {
		Stock stock = new Stock();
		stock.setIdentifier("1");
		stock.setTransactionType("Inventory");
		stock.setLocationId(locationId);
		Date accountabilityEndDate = new Date(2025,11,12);
		stock.setAccountabilityEndDate(accountabilityEndDate);
		return stock;
	}
}
