package org.opensrp.domain;

import org.joda.time.DateTime;
import org.opensrp.domain.Task.TaskStatus;

public class Campaign {

	private String identifier;

	private String title;

	private String description;

	private TaskStatus status;

	private ExecutionPeriod executionPeriod;

	private DateTime authoredOn;

	private DateTime lastModified;

	private String owner;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public ExecutionPeriod getExecutionPeriod() {
		return executionPeriod;
	}

	public void setExecutionPeriod(ExecutionPeriod executionPeriod) {
		this.executionPeriod = executionPeriod;
	}

	public DateTime getAuthoredOn() {
		return authoredOn;
	}

	public void setAuthoredOn(DateTime authoredOn) {
		this.authoredOn = authoredOn;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}

class ExecutionPeriod {
	private DateTime start;

	private DateTime end;

	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		this.start = start;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setEnd(DateTime end) {
		this.end = end;
	}

}
