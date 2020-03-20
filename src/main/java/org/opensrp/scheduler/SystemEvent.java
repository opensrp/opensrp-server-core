package org.opensrp.scheduler;

import com.google.gson.Gson;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class SystemEvent<T> {
	
	public static final String DATA = "data";
	
	public final String SUBJECT;
	
	protected T data;
	
	public SystemEvent(String subject, T data) {
		this.SUBJECT = subject;
		this.data = data;
	}
	
	public SystemEvent(Enum subject, T data) {
		this.SUBJECT = subject.name();
		this.data = data;
	}
	
	public MotechEvent toMotechEvent() {
		return toMotechEvent(null);
	}
	
	public MotechEvent toMotechEvent(Map<String, Object> parameters) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		parameters.put(DATA, new Gson().toJson(data));
		return new MotechEvent(SUBJECT, parameters);
	}
}
