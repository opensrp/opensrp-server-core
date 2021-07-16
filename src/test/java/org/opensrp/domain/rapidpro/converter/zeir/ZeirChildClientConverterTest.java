package org.opensrp.domain.rapidpro.converter.zeir;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.util.DateParserUtils;
import org.opensrp.util.SampleFullDomainObject;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;

public class ZeirChildClientConverterTest {

	private ZeirChildClientConverter zeirChildClientConverter;

	@Mock
	private IdentifierSourceService identifierSourceService;

	@Mock
	private UniqueIdentifierService identifierService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		zeirChildClientConverter = Mockito.spy(new ZeirChildClientConverter(identifierSourceService, identifierService));
	}

	@Test
	public void testConvertClientToContact() {
		Client client = SampleFullDomainObject.getClient();
		client.setBirthdate(DateParserUtils.parseZoneDateTime("2021-06-13T10:48:32.030Z"));
		RapidProContact rapidProContact = zeirChildClientConverter.convertClientToContact(client);
		Assert.assertNotNull(rapidProContact);
		Assert.assertEquals(RapidProConstants.CHILD, rapidProContact.getFields().getPosition());
	}
}
