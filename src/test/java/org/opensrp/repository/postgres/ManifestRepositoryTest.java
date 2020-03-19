package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.opensrp.domain.Manifest;
import org.opensrp.repository.ManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ManifestRepositoryTest extends BaseRepositoryTest {


    @Autowired
    private ManifestRepository manifestRepository;

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<String>();
        scripts.add("manifest.sql");
        return scripts;
    }

    private static Manifest initTestManifest() {
        Manifest manifest = new Manifest();
        String appVersion = "1234234";
        String json = "{}";
        String appId = "1234567op";

        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setIdentifier(23);
        manifest.setJson(json);
        manifest.setCreatedAt(new Date());
        return manifest;
    }

    private static Manifest initTestManifest2() {
        Manifest manifest = new Manifest();
        String appVersion = "12334";
        String json = "{}";
        String appId = "12567op";

        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setId(23);
        manifest.setJson(json);
        manifest.setCreatedAt(new Date());
        return manifest;
    }


    @Test
    public void testGet() {
        Manifest manifest = manifestRepository.get("1");
        assertEquals("{}", manifest.getJson());
        assertEquals("org.smartregister.giz", manifest.getAppId());
        assertEquals("0.0.1", manifest.getAppVersion());
    }

    @Test
    public void testGetWithNoIdentifier() {
        Manifest manifest = manifestRepository.get("");
        assertNull(manifest);
    }

    @Test
    public void testAdd() {
        Manifest manifest = new Manifest();
        manifest.setId(56);
        manifest.setAppId("12345");
        manifest.setAppVersion("1234567");
        manifest.setJson("{}");
        manifest.setCreatedAt(new Date());
        manifestRepository.add(manifest);

        assertEquals(6, manifestRepository.getAll().size());

        Manifest addedManifest = manifestRepository.get("56");
        assertNotNull(addedManifest);
        assertEquals("12345", addedManifest.getAppId());
        assertEquals("1234567", addedManifest.getAppVersion());
        assertEquals("{}", addedManifest.getJson());

    }

    @Test
    public void testEdit() {
        Manifest manifest = manifestRepository.get("5");
        String appVersion = "0.1.3";
        String appId = "org.smartregister.path";

        manifest.setAppVersion(appVersion);
        manifest.setAppId(appId);

        manifestRepository.update(manifest);

        Manifest updateManifest = manifestRepository.get("5");
        assertEquals(appVersion, updateManifest.getAppVersion());
        assertEquals(appId, updateManifest.getAppId());
    }

    @Test
    public void testGetManifestByAppId() {
        Manifest expectedManifest = initTestManifest2();
        manifestRepository.add(expectedManifest);

        Manifest actualManifest = manifestRepository.getManifestByAppId(expectedManifest.getAppId());
        assertNotNull(actualManifest);
        assertEquals(23, actualManifest.getId());
        assertEquals("12334", actualManifest.getAppVersion());
        assertEquals("12567op", actualManifest.getAppId());
        assertEquals("{}", actualManifest.getJson());
    }


    @Test
    public void testGetAllShouldGetAllManifest() {
        Manifest manifest1 = initTestManifest();
        manifestRepository.add(manifest1);

        Manifest manifest2 = initTestManifest2();
        manifestRepository.add(manifest2);

        List<Manifest> manifests = manifestRepository.getAll();
        assertNotNull(manifests);
        assertEquals(7, manifests.size());

        Set<Integer> ids = new HashSet<>();
        ids.add(manifest1.getId());
        ids.add(manifest2.getId());
        assertTrue(testIfAllIdsExists(manifests, ids));
    }

    private boolean testIfAllIdsExists(List<Manifest> manifests, Set<Integer> ids) {
        for (Manifest manifest : manifests) {
            ids.remove(manifest.getId());
        }
        return ids.size() == 0;
    }

}
