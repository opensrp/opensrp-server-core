package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensrp.repository.ErrorTraceRepository;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*","org.w3c.*"})
public class ErrorTraceServiceTest {

    private ErrorTraceService errorTraceService;
    private ErrorTraceRepository allErrorTrace;

    @Before
    public void setUp() {
        allErrorTrace = Mockito.mock(ErrorTraceRepository.class);
        errorTraceService = new ErrorTraceService(allErrorTrace);
    }

    @Test
    public void testGetAllErrors() {
        errorTraceService.getAllErrors();
        Mockito.verify(allErrorTrace).findAllErrors();
    }

    @Test
    public void testGetAllSolvedErrors() {
        errorTraceService.getAllSolvedErrors();
        Mockito.verify(allErrorTrace).findAllSolvedErrors();
    }

    @Test
    public void testGetAllUnSolvedErrors() {
        errorTraceService.getAllUnsolvedErrors();
        Mockito.verify(allErrorTrace).findAllUnSolvedErrors();
    }
}
