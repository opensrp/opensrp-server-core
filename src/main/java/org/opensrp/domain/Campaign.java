package org.opensrp.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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

	private long serverVersion;

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

	public long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(long serverVersion) {
		this.serverVersion = serverVersion;
	}

}

class ExecutionPeriod {
	private LocalDate start;

	private LocalDate end;

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

}
