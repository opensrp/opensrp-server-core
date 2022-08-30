package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.repository.ErrorTraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@ActiveProfiles(profiles = {"jedis"})
public class AllErrorTraceIntegrationTest {

    @Autowired
    private ErrorTraceRepository allErrorTrace;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldAddError() throws Exception {
        //ErrorTrace error=new ErrorTrace(new Date(), "Error Testing" , "not availalbe","this is an Testing Error", "unsolved");
        ErrorTrace error = new ErrorTrace();
        error.setId("1234");
        error.setErrorType("error loggging test");
        error.setDate(DateTime.now());
        error.setStackTrace("Complete Stack Trace :");
        error.setStatus("unsolved");
        error.setDocumentType("Test Document");
        //	error.setErrorType("test Error");
        allErrorTrace.add(error);

        assertEquals(error, allErrorTrace.get("1234"));

    }

}
