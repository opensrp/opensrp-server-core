package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.opensrp.domain.Manifest;
import org.opensrp.repository.ManifestRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
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


    @Test
    public void testGet() {
        Manifest manifest = manifestRepository.get("manifest12e5632");
        assertEquals("manifest12346", manifest.getIdentifier());
        assertEquals("{}", manifest.getJson());
        assertEquals("1234567", manifest.getAppId());
        assertEquals("12345", manifest.getAppVersion());
    }

    @Test
    public void testGetWithNoIdentifier() {

        Manifest manifest = manifestRepository.get("");
        assertNull(manifest);
    }


    @Test
    public void testAdd() {
        Manifest manifest = new Manifest();
        manifest.setIdentifier("manifest-12343");
        manifest.setAppId("12345");
        manifest.setAppVersion("1234567");
        manifest.setJson("{}");
        manifestRepository.add(manifest);

        assertEquals(1, manifestRepository.getAll().size());

        Manifest addedManifest = manifestRepository.get("manifest-12343");
        assertNotNull(addedManifest);
        assertEquals("12345", addedManifest.getAppId());
        assertEquals("1234567", addedManifest.getAppVersion());
        assertEquals("{}", addedManifest.getJson());

    }


    @Test
    public void testEdit() {
        Manifest manifest = manifestRepository.get("manifest-12343");
        manifest.setAppVersion("123456789876");
        manifest.setAppId("12345678");
        manifestRepository.update(manifest);

        Manifest updateManifest = manifestRepository.get("manifest-12343");
        assertNotNull(updateManifest);
    }
}
