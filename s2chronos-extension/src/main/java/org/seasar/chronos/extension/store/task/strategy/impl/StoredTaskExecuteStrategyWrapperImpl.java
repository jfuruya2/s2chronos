package org.seasar.chronos.extension.store.task.strategy.impl;

import org.seasar.chronos.core.task.strategy.TaskExecuteStrategy;
import org.seasar.chronos.core.task.strategy.TaskExecuteStrategyWrapper;
import org.seasar.chronos.extension.store.TaskStoreImpl;

public class StoredTaskExecuteStrategyWrapperImpl extends
		TaskExecuteStrategyWrapper {

	private TaskStoreImpl taskStore;

	public StoredTaskExecuteStrategyWrapperImpl(
			TaskExecuteStrategy taskExecuteStrategy) {
		super(taskExecuteStrategy);
	}

	@Override
	public void load() {
		this.taskStore.loadFromStore(this.getTaskId(), this);
	}

	@Override
	public void save() {
		this.taskStore.saveToStore(this);
	}

	public void setTaskStore(TaskStoreImpl taskStore) {
		this.taskStore = taskStore;
	}

}