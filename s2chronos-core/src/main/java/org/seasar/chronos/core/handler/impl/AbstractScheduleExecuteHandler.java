package org.seasar.chronos.core.handler.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.seasar.chronos.core.event.SchedulerEventHandler;
import org.seasar.chronos.core.handler.ScheduleExecuteHandler;
import org.seasar.chronos.core.schedule.TaskScheduleEntryManager;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.log.Logger;

public abstract class AbstractScheduleExecuteHandler implements
		ScheduleExecuteHandler {

	protected static final Logger log = Logger
			.getLogger(AbstractScheduleExecuteHandler.class);

	protected TaskScheduleEntryManager taskContenaStateManager = TaskScheduleEntryManager
			.getInstance();

	protected ExecutorService executorService;

	protected SchedulerEventHandler schedulerEventHandler;

	@Binding(bindingType = BindingType.NONE)
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	protected AtomicBoolean pause;

	protected AtomicBoolean paused;

	public void setPause(AtomicBoolean pause) {
		this.pause = pause;
	}

	public abstract void handleRequest() throws InterruptedException;

	public void setSchedulerEventHandler(
			SchedulerEventHandler schedulerEventHandler) {
		this.schedulerEventHandler = schedulerEventHandler;
	}

	public void setPaused(AtomicBoolean paused) {
		this.paused = paused;
	}

}
