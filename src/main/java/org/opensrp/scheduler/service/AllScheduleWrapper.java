package org.opensrp.scheduler.service;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllScheduleWrapper extends AllSchedules {
	
	@Autowired
	public AllScheduleWrapper(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db,
	    TrackedSchedulesJsonReader trackedSchedulesJsonReader, ScheduleFactory scheduleFactory) {
		super(db, trackedSchedulesJsonReader, scheduleFactory);
	}
	
	@View(name = "by_schedule_name", map = "function(doc) { if(doc.type === 'ScheduleRecord') emit(doc.name); }")
	public ScheduleRecord getRecordByName(String name) {
		List<ScheduleRecord> records = queryView("by_schedule_name", name);
		if (records.isEmpty())
			return null;
		return records.get(0);
	}
}
