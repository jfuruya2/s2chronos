package org.seasar.chronos.core.task;

import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.ThreadPoolType;

public interface TaskPropertyWriter {

	public boolean hasTaskId();

	public boolean hasTaskName();

	public boolean hasDescription();

	public boolean hasStartTask();

	public boolean hasEndTask();

	public boolean hasShutdownTask();

	public boolean hasExecuted();

	public boolean hasReSchedule();

	public boolean hasTrigger();

	public boolean hasThreadPoolSize();

	public boolean hasThreadPoolType();

	public void setTaskId(Long value);

	public void setTaskName(String value);

	public void setDescription(String value);

	public void setStartTask(boolean value);

	public void setEndTask(boolean value);

	public void setShutdownTask(boolean value);

	public void setExecuted(boolean value);

	public void setReSchedule(boolean value);

	public void setTrigger(TaskTrigger value);

	public void setThreadPoolSize(int value);

	public void setThreadPoolType(ThreadPoolType value);

	public abstract void loadTask(Object task, Class<?> taskClass);

}