package org.opensrp.repository.it;

import org.junit.Before;
import org.mockito.Mock;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.scheduler.AlertCreationAction;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.util.TestResourceLoader;

import java.io.IOException;

import static org.mockito.MockitoAnnotations.initMocks;

public class ActionCreationIntegrationTest extends TestResourceLoader {
	
	public ActionCreationIntegrationTest() throws IOException {
		super();
	}
	
	@Mock
	private HealthSchedulerService scheduler;
	
	private AlertCreationAction reminderAction;
	
	@Mock
	private FormSubmissionService formSubmissionService;
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		
	}
	
	public static void main(String[] args) {
		System.out.println(Boolean.valueOf("0"));
	}
	
}
