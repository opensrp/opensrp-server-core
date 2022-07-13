package org.opensrp.repository.postgres;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.opensrp.domain.viewconfiguration.LoginConfiguration;
import org.opensrp.domain.viewconfiguration.View;
import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.repository.ViewConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ViewConfigurationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    @Qualifier("viewConfigurationRepositoryPostgres")
    private ViewConfigurationRepository viewConfigurationRepository;

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        scripts.add("view_configuration.sql");
        return scripts;
    }

    @Test
    public void testGet() {
        ViewConfiguration view = viewConfigurationRepository.get("19a2e8aa6739d77a2b780199c6122867");
        assertEquals("main", view.getIdentifier());
        assertEquals(1516890951253l, view.getServerVersion().longValue());

        assertNull(viewConfigurationRepository.get("19a2e8aa6739d22867"));

    }

    @Test
    public void testAdd() {
//		long now = System.currentTimeMillis();
        long lastSyncedVersion = 1517890951252l;
        ViewConfiguration viewConfiguration = new ViewConfiguration();
        viewConfiguration.setIdentifier("help");
        View view = new View();
        view.setIdentifier("faq");
        view.setLabel("FAQ");
        view.setVisible(true);
        view.setOrientation("vertical");
        viewConfiguration.setViews(new ArrayList<View>());
        viewConfiguration.getViews().add(view);
        viewConfiguration.setServerVersion(1518890951252l);

        viewConfigurationRepository.add(viewConfiguration);

        assertEquals(6, viewConfigurationRepository.getAll().size());

        List<ViewConfiguration> savedViews = viewConfigurationRepository.findViewConfigurationsByVersion(lastSyncedVersion);
        assertEquals(1, savedViews.size());
        assertEquals(1, savedViews.get(0).getViews().size());
        assertEquals("faq", savedViews.get(0).getViews().get(0).getIdentifier());
        MatcherAssert.assertThat(savedViews, Matchers.contains(Matchers.hasProperty("serverVersion", Matchers.greaterThan(0l))));
    }

    @Test
    public void testUpdate() {
        ViewConfiguration view = viewConfigurationRepository.get("92141b17040021a7ce326194ff0029f7");
        LoginConfiguration configuration = (LoginConfiguration) view.getMetadata();
        configuration.setShowPasswordCheckbox(false);
        configuration.setLanguage("fr_cn");
        configuration.setLogoUrl("http://localhost:98778/test.jpg");
        view.setMetadata(configuration);
        long serverVersion = view.getServerVersion();
        viewConfigurationRepository.update(view);


        ViewConfiguration updatedView = viewConfigurationRepository
                .get("92141b17040021a7ce326194ff0029f7");
        LoginConfiguration updatedConfiguration = (LoginConfiguration) updatedView.getMetadata();
        assertEquals("fr_cn", updatedConfiguration.getLanguage());
        assertEquals("http://localhost:98778/test.jpg", updatedConfiguration.getLogoUrl());
        assertFalse(updatedConfiguration.getShowPasswordCheckbox());
        MatcherAssert.assertThat(updatedView.getServerVersion(), Matchers.greaterThan(serverVersion));

    }

    @Test
    public void testGetAll() {
        assertEquals(5, viewConfigurationRepository.getAll().size());

        viewConfigurationRepository.safeRemove(viewConfigurationRepository.get("3a065d7c3354eb2bc23c8a3bc303ef62"));

        assertEquals(4, viewConfigurationRepository.getAll().size());
    }

    @Test
    public void testSafeRemove() {

        ViewConfiguration view = viewConfigurationRepository.get("3a065d7c3354eb2bc23c8a3bc303ef62");
        viewConfigurationRepository.safeRemove(view);

        assertNull(viewConfigurationRepository.get(view.getId()));

        List<ViewConfiguration> views = viewConfigurationRepository.getAll();
        for (ViewConfiguration v : views) {
            assertNotEquals(view.getId(), v.getId());
            assertNotEquals(view.getIdentifier(), v.getIdentifier());
        }
    }

    @Test
    public void testFindAllViewConfigurations() {
        assertEquals(5, viewConfigurationRepository.getAll().size());

        ViewConfiguration viewConfiguration = new ViewConfiguration();
        viewConfiguration.setIdentifier("bmi");

        viewConfigurationRepository.add(viewConfiguration);

        assertEquals(6, viewConfigurationRepository.getAll().size());
    }

    @Test
    public void testFindViewConfigurationsByVersion() {
        assertEquals(5, viewConfigurationRepository.findViewConfigurationsByVersion(0l).size());

        List<ViewConfiguration> views = viewConfigurationRepository.findViewConfigurationsByVersion(1516614392971l);
        assertEquals(2, views.size());

        for (ViewConfiguration view : views) {
            assertTrue(view.getServerVersion() >= 1516614392971l);
            assertTrue(view.getIdentifier().equals("positive_register_header") || view.getIdentifier().equals("main"));
        }

    }

    @Test
    public void testFindByEmptyServerVersion() {
        assertTrue(viewConfigurationRepository.findByEmptyServerVersion().isEmpty());

        ViewConfiguration view = viewConfigurationRepository.get("d243bc5737fb389e52601cb850299541");
        view.setServerVersion(0l);
        viewConfigurationRepository.update(view);

        List<ViewConfiguration> views = viewConfigurationRepository.findByEmptyServerVersion();
        assertEquals(0, views.size());


        ViewConfiguration view2 = viewConfigurationRepository.get("3a065d7c3354eb2bc23c8a3bc303ba20");
        view2.setServerVersion(0l);
        viewConfigurationRepository.update(view2);

        views = viewConfigurationRepository.findByEmptyServerVersion();
        assertEquals(0, views.size());

    }

}
