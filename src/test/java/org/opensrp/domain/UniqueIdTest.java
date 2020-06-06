package org.opensrp.domain;

import org.junit.Test;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import static org.junit.Assert.assertEquals;

public class UniqueIdTest {
	
	@Test
	public void testGetterAndSetter() {
		Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
		
		validator.validate(PojoClassFactory.getPojoClass(UniqueId.class));

		assertEquals(19,PojoClassFactory.getPojoClass(UniqueId.class).getPojoFields().size());
	}
	
}
