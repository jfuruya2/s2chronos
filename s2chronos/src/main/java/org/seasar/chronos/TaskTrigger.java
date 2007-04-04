package org.seasar.chronos;

public interface TaskTrigger {

	public void save();

	public void load();

	public boolean getStartTask();

	public void setStartTask(boolean startTask);

	public boolean getEndTask();

	public void setEndTask(boolean endTask);

	public String getDescription();

	public long getId();

	public Object getTask();

	public String getName();

	public boolean isExecuted();

	public void setDescription(String description);

	public void setExecuted(boolean executed);

	public void setId(long triggerId);

	public void setTask(Object task);

	public void setName(String name);

}