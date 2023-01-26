package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.opensrp.common.AllConstants.Config;
import org.opensrp.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles(profiles = { "jedis"})
public class ConfigServiceIntegrationTest {
	
	public enum TestToken {
		token1_int(Integer.valueOf(12)),
		token2_long(Long.valueOf(1234567890123L)),
		token3_string("test string"),
		token5_datetime(LocalDate.now()),
		token6_float(Float.valueOf("21212.3323")),
		token7_double(Double.valueOf(43748384.347384738)),
		token9_boolean(true);
		
		private Object value;
		
		public Object value() {
			return value;
		}
		
		private TestToken(Object value) {
			this.value = value;
		}
	}
	
	@Autowired
	private AppStateTokensRepositoryImpl alltokens;
	
	private static AppStateTokensRepositoryImpl allAppTokens;
	
	@Autowired
	private ConfigService configService;
	
	
	@Before
	public void setUp() {
		if (allAppTokens == null) {
			alltokens.removeAll();
			allAppTokens = alltokens;
			
			for (TestToken tk : TestToken.values()) {
				configService.registerAppStateToken(tk, tk.value(), tk.name(), false);
			}
			
			for (TestToken tk : TestToken.values()) {
				assertEquals(tk.name(), configService.getAppStateTokenByName(tk).getName());
			}
		}
	}
	
	@AfterClass
	public static void cleanup() {
		allAppTokens.removeAll();
	}
	
	@Test
	public void shouldGetIntToken() {
		TestToken tk = TestToken.token1_int;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).intValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForRegisteringDuplicateEntryFlaggedAsDonotSuppressException() {
		configService.registerAppStateToken(TestToken.token1_int, "", "token1_int", false);
	}
	
	@Test
	public void shouldNotThrowExceptionForRegisteringDuplicateEntryFlaggedAsSuppressException() {
		configService.registerAppStateToken(TestToken.token1_int, "", "token1_int", true);
		assertNotNull(configService.getAppStateTokenByName(TestToken.token1_int));
		assertEquals("token1_int",configService.getAppStateTokenByName(TestToken.token1_int).getDescription());
		assertEquals(TestToken.token1_int.value,Integer.valueOf(configService.getAppStateTokenByName(TestToken.token1_int).getValue().toString()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForRegisteringWithMissingDescription() {
		configService.registerAppStateToken(TestToken.token1_int, "", "", false);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForRegisteringWithMissingName() {
		configService.registerAppStateToken(null, "", "", false);
	}
	
	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionForUpdatingWithNonExistentName() {
		configService.updateAppStateToken(Config.FORM_ENTITY_PARSER_LAST_SYNCED_FORM_SUBMISSION, "");
	}
	
	@Test
	public void shouldGetLongToken() {
		TestToken tk = TestToken.token2_long;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).longValue());
	}
	
	@Test
	public void shouldGetStringToken() {
		TestToken tk = TestToken.token3_string;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).stringValue());
	}
	
	@Test
	public void shouldGetDatetimeToken() {
		TestToken tk = TestToken.token5_datetime;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).datetimeValue());
	}
	
	@Test
	public void shouldGetFloatToken() {
		TestToken tk = TestToken.token6_float;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).floatValue());
	}
	
	@Test
	public void shouldGetDoubleToken() {
		TestToken tk = TestToken.token7_double;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).doubleValue());
	}
	
	@Test
	public void shouldGetBooleanToken() {
		TestToken tk = TestToken.token9_boolean;
		assertEquals(tk.value(), configService.getAppStateTokenByName(tk).booleanValue());
	}
	
}
