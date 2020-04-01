package org.opensrp.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ManifestTest {

    @Test
    public void testConstructor() {
        String identifier = "mani1234";
        String appVersion = "1234234";
        String json = "{}";
        String appId = "1234567op";
        
        Manifest manifest = new Manifest();
        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setIdentifier(identifier);
        manifest.setJson(json);
        
        Manifest manifest1 = (Manifest) manifest;
        assertEquals(identifier, manifest1.getIdentifier());
        assertEquals(appVersion, manifest1.getAppVersion());
        assertEquals(appId, manifest1.getAppId());
        assertEquals(json, manifest1.getJson());
    }

}
