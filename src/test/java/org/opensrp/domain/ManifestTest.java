package org.opensrp.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ManifestTest {

    @Test
    public void testConstructor() {
        int identifier = 23;
        String appVersion = "1234234";
        String json = "{}";
        String appId = "1234567op";
        
        Manifest manifest = new Manifest();
        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setId(identifier);
        manifest.setJson(json);

        assertEquals(identifier, manifest.getId());
        assertEquals(appVersion, manifest.getAppVersion());
        assertEquals(appId, manifest.getAppId());
        assertEquals(json, manifest.getJson());

    }

}
