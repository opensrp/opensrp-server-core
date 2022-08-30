package org.opensrp.repository.postgres;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.smartregister.domain.ProductCatalogue;
import org.opensrp.repository.ProductCatalogueRepository;
import org.opensrp.search.ProductCatalogueSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProductCatalogueRepositoryTest extends BaseRepositoryTest {

    @Autowired
    @Qualifier("productCatalogueRepositoryPostgres")
    private ProductCatalogueRepository productCatalogueRepository;

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<String>();
        scripts.add("product_catalogue.sql");
        return scripts;
    }

    @Test
    public void testAdd() {
        ProductCatalogue productCatalogue = createProductCatalogue();
        productCatalogueRepository.add(productCatalogue);
        List<ProductCatalogue> productCatalogueList = productCatalogueRepository.getAll("http://localhost:8080/opensrp");
        assertEquals(2, productCatalogueList.size());
        for (ProductCatalogue pc : productCatalogueList) {
            MatcherAssert.assertThat(pc.getServerVersion(), Matchers.greaterThanOrEqualTo(123344l));
        }
    }

    @Test
    public void testUpdate() {
        ProductCatalogue productCatalogue = productCatalogueRepository.getById(1l, "http://localhost:8080/opensrp");
        productCatalogue.setProductName("Updated product name");
        long serverVersion = productCatalogue.getServerVersion();
        productCatalogueRepository.update(productCatalogue);
        ProductCatalogue updated = productCatalogueRepository.getById(1l, "http://localhost:8080/opensrp");
        assertNotNull(updated);
        assertEquals("Updated product name", updated.getProductName());
        MatcherAssert.assertThat(updated.getServerVersion(), Matchers.greaterThan(serverVersion));
    }

    @Test
    public void testGetAll() {
        List<ProductCatalogue> productCatalogues = productCatalogueRepository.getAll("http://localhost:8080/opensrp");
        assertEquals(1, productCatalogues.size());
    }

    @Test
    public void testSafeRemove() {
        ProductCatalogue productCatalogue = productCatalogueRepository.getById(1l, "http://localhost:8080/opensrp");
        productCatalogueRepository.safeRemove(productCatalogue);
        List<ProductCatalogue> productCatalogues = productCatalogueRepository.getAll("http://localhost:8080/opensrp");
        assertEquals(0, productCatalogues.size());
    }

    @Test
    public void testSafeRemoveById() {
        productCatalogueRepository.safeRemove(1l);
        List<ProductCatalogue> productCatalogues = productCatalogueRepository.getAll("http://localhost:8080/opensrp");
        assertEquals(0, productCatalogues.size());
    }

    @Test
    public void testGetById() {
        ProductCatalogue productCatalogue = productCatalogueRepository.getById(1l, "http://localhost:8080/opensrp");
        assertEquals("Midwifery Kit", productCatalogue.getProductName());
    }

    @Test
    public void testGetProductCataloguesBySearchBeanWithLimit() {
        ProductCatalogueSearchBean productCatalogueSearchBean = new ProductCatalogueSearchBean();
        productCatalogueSearchBean.setUniqueId(1l);
        int limit = Integer.MAX_VALUE;
        List<ProductCatalogue> productCatalogues = productCatalogueRepository.getProductCataloguesBySearchBean(productCatalogueSearchBean, limit, "http://localhost:8080/opensrp");
        assertEquals(1, productCatalogues.size());
        assertEquals("Midwifery Kit", productCatalogues.get(0).getProductName());

    }

    @Test
    public void testGetProductCataloguesBySearchBeanWithNoLimitSet() {
        ProductCatalogueSearchBean productCatalogueSearchBean = new ProductCatalogueSearchBean();
        productCatalogueSearchBean.setUniqueId(1l);
        List<ProductCatalogue> productCatalogues = productCatalogueRepository.getProductCataloguesBySearchBean(productCatalogueSearchBean, "http://localhost:8080/opensrp");
        assertEquals(1, productCatalogues.size());
        assertEquals("Midwifery Kit", productCatalogues.get(0).getProductName());

    }

    private ProductCatalogue createProductCatalogue() {
        ProductCatalogue productCatalogue = new ProductCatalogue();
        ArrayList<String> sections = new ArrayList<>();
        sections.add("Health");
        productCatalogue.setProductName("Product A");
        productCatalogue.setAvailability("available");
        productCatalogue.setCondition("good condition");
        productCatalogue.setAppropriateUsage("staff is trained to use it appropriately");
        productCatalogue.setAccountabilityPeriod(1);
        productCatalogue.setServerVersion(123456l);
        return productCatalogue;
    }

}
