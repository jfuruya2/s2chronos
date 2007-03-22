package org.seasar.chronos.handler.impl;

import java.util.concurrent.ExecutorService;

import org.seasar.chronos.handler.ScheduleExecuteHandler;
import org.seasar.chronos.impl.TaskContenaStateManager;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.log.Logger;

public abstract class AbstractScheduleExecuteHandler implements
		ScheduleExecuteHandler {

	protected static Logger log = Logger
			.getLogger(AbstractScheduleExecuteHandler.class);

	protected TaskContenaStateManager taskContenaStateManager = TaskContenaStateManager
			.getInstance();

	protected ExecutorService executorService;

	@Binding(bindingType = BindingType.NONE)
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public abstract void handleRequest() throws InterruptedException;

}