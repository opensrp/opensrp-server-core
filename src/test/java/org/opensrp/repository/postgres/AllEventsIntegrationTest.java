package org.opensrp.repository.postgres;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.service.EventService;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@ActiveProfiles(profiles = {"jedis"})
public class AllEventsIntegrationTest extends BaseRepositoryTest {

    //TODO Detailed testing
    @Autowired
    private EventService eventService;

    private String username = "johndoe";

    @Override
    protected Set<String> getDatabaseScripts() {
        return null;
    }

    @Before
    public void setUp() throws Exception {
        tableNames = Collections.singletonList("core.event");
        truncateTables();
        initMocks(this);
        ReflectionTestUtils.setField(eventService, "isPlanEvaluationEnabled", true);
    }

    private void addEvents() {
        for (int i = 0; i < 20; i++) {
            Event e = new Event("entityid" + i, "Immunization", new DateTime(), "testentity", "demotest", "location" + i,
                    "formSubmission" + i + 100);
            e.addObs(new Obs("concept", "txt", "1025AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "bcg"));
            e.addObs(new Obs("concept", "txt", "1026AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "penta1"));
            e.addObs(new Obs("concept", "txt", "1027AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "penta2"));
            e.addObs(new Obs("concept", "txt", "1028AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "penta3"));
            e.addObs(new Obs("concept", "txt", "1029AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "mealses1"));
            e.addObs(new Obs("concept", "txt", "1030AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "measles2"));
            e.addObs(new Obs("concept", "txt", "1029AAAAAAAAAAAAAAAA", null, "2015-01-01", "comments test" + i, "tt1"));
            e.addObs(new Obs("concept", "txt", "1030AAAAAAAAAAAAAAAA", null, "2016-02-01", "comments test" + i, "tt2"));
            eventService.addEvent(e, username);
        }
    }

    @Test
    public void test() {
        addEvents();
        assertTrue(eventService.getByBaseEntityAndFormSubmissionId("entityid0", "formSubmission0100") != null);
    }
}
