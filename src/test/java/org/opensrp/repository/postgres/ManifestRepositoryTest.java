package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.opensrp.domain.Manifest;
import org.opensrp.repository.ManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
        String identifier = "7";
        String appVersion = "1234234";
        String json = "{}";
        String appId = "1234567op";

        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setIdentifier(identifier);
        manifest.setJson(json);
        manifest.setCreatedAt(new DateTime());
        return manifest;
    }

    private static Manifest initTestManifest2() {
        Manifest manifest = new Manifest();
        String identifier = "6";
        String appVersion = "12334";
        String json = "{}";
        String appId = "12567op";

        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setIdentifier(identifier);
        manifest.setJson(json);
        manifest.setCreatedAt(new DateTime());
        return manifest;
    }


    @Test
    public void testGet() {
        Manifest manifest = manifestRepository.get("1");
        assertEquals("1", manifest.getIdentifier());
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
        manifest.setIdentifier("2");
        manifest.setAppId("12345");
        manifest.setAppVersion("1234567");
        manifest.setJson("{}");
        manifest.setCreatedAt(new DateTime());
        manifestRepository.add(manifest);

        assertEquals(2, manifestRepository.getAll().size());

        Manifest addedManifest = manifestRepository.get("2");
        assertNotNull(addedManifest);
        assertEquals("12345", addedManifest.getAppId());
        assertEquals("1234567", addedManifest.getAppVersion());
        assertEquals("{}", addedManifest.getJson());

    }

    @Test
    public void testEdit() {
        Manifest manifest = manifestRepository.get("1");
        manifest.setAppVersion("123456789876");
        manifest.setAppId("12345678");
        manifestRepository.update(manifest);

        Manifest updatedManifest = manifestRepository.get("1");
        assertNotNull(updatedManifest);
        assertEquals("12345678", updatedManifest.getAppId());
        assertEquals("123456789876", updatedManifest.getAppVersion());
    }

    @Test
    public void testGetManifestByAppId() {
        Manifest expectedManifest = initTestManifest2();
        Manifest lowerVersionManifest2 = initTestManifest2();
        Manifest lowerVersionManifest1 = initTestManifest2();

        lowerVersionManifest1.setAppVersion("10001");
        lowerVersionManifest1.setIdentifier("6");

        expectedManifest.setIdentifier("7");

        lowerVersionManifest2.setAppVersion("10000");
        lowerVersionManifest2.setIdentifier("8");

        manifestRepository.add(lowerVersionManifest1);
        manifestRepository.add(expectedManifest);
        manifestRepository.add(lowerVersionManifest2);

        Manifest actualManifest = manifestRepository.getManifestByAppId(expectedManifest.getAppId());
        assertNotNull(actualManifest);
        assertEquals("7", actualManifest.getIdentifier());
        assertEquals("12334", actualManifest.getAppVersion());
        assertEquals("12567op", actualManifest.getAppId());
        assertEquals("{}", actualManifest.getJson());
    }

    @Test
    public void testGetManifestsByAppId() {
        Manifest expectedManifest = initTestManifest2();
        Manifest lowerVersionManifest2 = initTestManifest2();
        Manifest lowerVersionManifest1 = initTestManifest2();

        lowerVersionManifest1.setAppVersion("10001");
        lowerVersionManifest1.setIdentifier("6");

        expectedManifest.setIdentifier("7");

        lowerVersionManifest2.setAppVersion("10000");
        lowerVersionManifest2.setIdentifier("8");

        manifestRepository.add(lowerVersionManifest1);
        manifestRepository.add(expectedManifest);
        manifestRepository.add(lowerVersionManifest2);

        List<Manifest> manifestList = manifestRepository.getManifestsByAppId(expectedManifest.getAppId());
        assertEquals(3, manifestList.size());
    }


    @Test
    public void testGetAllShouldGetAllManifest() {
        Manifest manifest2 = initTestManifest2();
        manifestRepository.add(manifest2);

        Manifest manifest1 = initTestManifest();
        manifestRepository.add(manifest1);

        List<Manifest> manifests = manifestRepository.getAll();
        assertNotNull(manifests);
        assertEquals(3, manifests.size());

        Set<String> ids = new HashSet<>();
        ids.add(manifest1.getIdentifier());
        ids.add(manifest2.getIdentifier());
        assertTrue(testIfAllIdsExists(manifests, ids));
    }

    @Test
    public void testGetAllShouldGetAllManifestsWithOrdering() {
        Manifest manifest = initTestManifest();
        manifestRepository.add(manifest);

        Manifest manifest2 = initTestManifest2();
        manifestRepository.add(manifest2);

        List<Manifest> manifests = manifestRepository.getAll(1);
        assertNotNull(manifests);
        assertEquals(1, manifests.size());

        Set<String> ids = new HashSet<>();
        assertEquals("6", manifests.get(0).getIdentifier());
        ids.add(manifest2.getIdentifier());
        assertTrue(testIfAllIdsExists(manifests, ids));
    }

    @Test
    public void testSafeRemove() {
        Manifest manifestToRemove = manifestRepository.get("1");
        assertNotNull(manifestToRemove);

        manifestRepository.safeRemove(manifestToRemove);

        assertNull(manifestRepository.get("1"));
    }

    @Test
    public void testGetByAppIdAndAppVersion() {
        Manifest manifest = new Manifest();
        manifest.setIdentifier("0.0.5r34");
        manifest.setAppId("org.smartregister.zeir");
        manifest.setAppVersion("0.0.5");
        manifest.setCreatedAt(new DateTime());

        manifestRepository.add(manifest);

        Manifest resultManifest = manifestRepository.getManifest("org.smartregister.zeir", "0.0.5");
        assertEquals("0.0.5r34", resultManifest.getIdentifier());
        assertEquals("org.smartregister.zeir", resultManifest.getAppId());
        assertEquals("0.0.5", resultManifest.getAppVersion());
    }

    private boolean testIfAllIdsExists(List<Manifest> manifests, Set<String> ids) {
        for (Manifest manifest : manifests) {
            ids.remove(manifest.getIdentifier());
        }
        return ids.size() == 0;
    }

}
