package org.opensrp.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class ManifestTest {
  /*  @Test
    public void testGetterAndSetter() {
       Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();

      validator.validate(PojoClassFactory.getPojoClass(Manifest.class));
    }*/

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
