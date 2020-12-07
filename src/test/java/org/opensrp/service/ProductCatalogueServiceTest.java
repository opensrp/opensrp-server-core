package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.domain.ProductCatalogue;
import org.opensrp.repository.ProductCatalogueRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.search.ProductCatalogueSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProductCatalogueServiceTest extends BaseRepositoryTest {

	private ProductCatalogueService productCatalogueService;

	@Autowired
	@Qualifier("productCatalogueRepositoryPostgres")
	private ProductCatalogueRepository productCatalogueRepository;

	@Before
	public void setUpPostgresRepository() {
		productCatalogueService = new ProductCatalogueService(productCatalogueRepository);
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("product_catalogue.sql");
		return scripts;
	}

	@Test
	public void testFindAllProductCatalogs() {
		ProductCatalogueSearchBean productCatalogueSearchBean = new ProductCatalogueSearchBean();
		productCatalogueSearchBean.setProductName("");
		productCatalogueSearchBean.setUniqueId(0l);
		List<ProductCatalogue> productCatalogueList = productCatalogueService
				.getProductCatalogues(productCatalogueSearchBean, "http://localhost:8080/opensrp");
		assertEquals(1, productCatalogueList.size());
	}

	@Test
	public void testAdd() {
		ProductCatalogue productCatalogue = createProductCatalogue();
		productCatalogueService.add(productCatalogue);
		List<ProductCatalogue> productCatalogues = productCatalogueService.findAllProductCatalogues("http://localhost:8080/opensrp");
		assertEquals(2, productCatalogues.size());
	}

	@Test
	public void testUpdate() {
		ProductCatalogue productCatalogue = productCatalogueService.getProductCatalogue(1l, "http://localhost:8080/opensrp");
		productCatalogue.setProductName("Updated Product Name");
		productCatalogue.setPhotoURL("/multimedia/media/1");
		productCatalogueService.update(productCatalogue);
		ProductCatalogue updatedProductCatalogue = productCatalogueService.getProductCatalogue(1l, "http://localhost:8080/opensrp");
		assertEquals("Updated Product Name", updatedProductCatalogue.getProductName());
		assertEquals("http://localhost:8080/opensrp/multimedia/media/1", updatedProductCatalogue.getPhotoURL());
	}

	@Test
	public void testGetProductCatalogueByName() {
		ProductCatalogue productCatalogue = productCatalogueService.getProductCatalogueByName("Midwifery Kit");
		assertEquals(new Long(1l), productCatalogue.getUniqueId());
		assertEquals(true, productCatalogue.getIsAttractiveItem());
		assertEquals("AX-123", productCatalogue.getMaterialNumber());
	}

	@Test
	public void testDeleteProductCatalogueById() {
		productCatalogueService.deleteProductCatalogueById(1l);
		List<ProductCatalogue> productCatalogues = productCatalogueService.findAllProductCatalogues("http://localhost:8080/opensrp");
		assertEquals(0, productCatalogues.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteProductCatalogueByInvalidId() {
		productCatalogueService.deleteProductCatalogueById(0l);
	}

	private ProductCatalogue createProductCatalogue() {
		ProductCatalogue productCatalogue = new ProductCatalogue();
		productCatalogue.setProductName("Product A");
		productCatalogue.setIsAttractiveItem(Boolean.TRUE);
		productCatalogue.setMaterialNumber("MT-123");
		productCatalogue.setAvailability("available");
		productCatalogue.setCondition("good condition");
		productCatalogue.setAppropriateUsage("staff is trained to use it appropriately");
		productCatalogue.setAccountabilityPeriod(1);
		productCatalogue.setServerVersion(123456l);
		return productCatalogue;
	}

}
