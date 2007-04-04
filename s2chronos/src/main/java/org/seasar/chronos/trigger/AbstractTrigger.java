package org.seasar.chronos.trigger;

import org.seasar.chronos.TaskTrigger;

public abstract class AbstractTrigger implements TaskTrigger {

	public void load() {

	}

	public void save() {

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractTrigger) {
			boolean result = true;
			AbstractTrigger src = (AbstractTrigger) obj;
			result = result & this.id == src.id;
			if (this.name != null) {
				result = result & this.name.equals(src.name);
			}
			if (this.task != null) {
				result = result & this.task.equals(src.task);
			}
			if (this.description != null) {
				result = result & this.description.equals(src.description);
			}
			result = result & this.executed == src.executed;
			return result;
		} else {
			return super.equals(obj);
		}
	}

	private long id;

	private String name;

	private Object task;

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

	public Object getTask() {
		return task;
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

	public void setTask(Object task) {
		this.task = task;
	}

	public void setName(String name) {
		this.name = name;
	}

}
