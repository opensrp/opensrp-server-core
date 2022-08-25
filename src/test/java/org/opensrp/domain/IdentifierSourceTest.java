package org.opensrp.domain;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdentifierSourceTest {

    @Test
    public void testGetterAndSetter() {
        Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();

        validator.validate(PojoClassFactory.getPojoClass(IdentifierSource.class));
        assertEquals(12, PojoClassFactory.getPojoClass(IdentifierSource.class).getPojoFields().size());
    }
}
