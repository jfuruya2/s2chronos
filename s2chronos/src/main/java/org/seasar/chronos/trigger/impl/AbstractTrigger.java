package org.seasar.chronos.trigger.impl;

import org.seasar.chronos.trigger.Trigger;

public abstract class AbstractTrigger implements Trigger {

	private long id;

	private String name;

	private Object job;

	private String description;

	private boolean executed;

	public AbstractTrigger() {

	}

	public AbstractTrigger(String name) {
		this.setName(name);
	}

	public String getDescription() {
		return description;
	}

	public long getId() {
		return this.id;
	}

	public Object getJob() {
		return job;
	}

	public String getName() {
		return name;
	}

	public boolean isExecuted() {
		return this.executed;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setJob(Object jobComponent) {
		this.job = jobComponent;
	}

	public void setName(String name) {
		this.name = name;
	}

}
