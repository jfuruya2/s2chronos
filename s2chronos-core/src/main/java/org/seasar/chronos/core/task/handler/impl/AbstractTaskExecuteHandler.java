/*
 * Copyright 2007-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.chronos.core.task.handler.impl;

import org.seasar.chronos.core.delegate.MethodInvoker;
import org.seasar.chronos.core.model.TaskTrigger;
import org.seasar.chronos.core.task.Transition;
import org.seasar.chronos.core.task.handler.TaskExecuteHandler;
import org.seasar.chronos.core.task.impl.TaskMethodManager;
import org.seasar.chronos.core.task.strategy.TaskExecuteStrategy;
import org.seasar.framework.log.Logger;

public abstract class AbstractTaskExecuteHandler implements TaskExecuteHandler {

	protected static final String METHOD_PREFIX_NAME_DO = "do";

	protected static final String METHOD_PREFIX_NAME_START = "start";

	protected static final String METHOD_PREFIX_NAME_END = "end";

	@SuppressWarnings("unused")
	private static Logger log = Logger
			.getLogger(AbstractTaskExecuteHandler.class);

	private MethodInvoker methodInvoker;

	public TaskMethodManager taskMethodManager;

	public abstract Transition handleRequest(String startTaskName)
			throws InterruptedException;

	private TaskExecuteStrategy taskExecuteStrategy;

	public void setTaskExecuteStrategy(TaskExecuteStrategy taskExecuteStrategy) {
		this.taskExecuteStrategy = taskExecuteStrategy;
	}

	public TaskExecuteStrategy getTaskExecuteStrategy() {
		return this.taskExecuteStrategy;
	}

	public void setMethodInvoker(MethodInvoker methodInvoker) {
		this.methodInvoker = methodInvoker;
	}

	public MethodInvoker getMethodInvoker() {
		return methodInvoker;
	}

	public void setMethodGroupMap(TaskMethodManager taskMethodManager) {
		this.taskMethodManager = taskMethodManager;
	}

	public TaskMethodManager getMethodGroupMap() {
		return taskMethodManager;
	}

	protected String toMethodName(String taskName) {
		StringBuffer sb = new StringBuffer();
		String firstChar = taskName.substring(0, 1);
		String afterString = taskName.substring(1);
		sb.append(METHOD_PREFIX_NAME_DO);
		sb.append(firstChar.toUpperCase());
		sb.append(afterString);
		return sb.toString();
	}

	protected String toTaskName(String functionName) {
		StringBuffer sb = new StringBuffer("");
		if (functionName.startsWith(METHOD_PREFIX_NAME_DO)) {
			functionName = functionName.substring(2);
			String firstChar = functionName.substring(0, 1).toLowerCase();
			String afterString = functionName.substring(1);
			sb.append(firstChar).append(afterString);
		}
		return sb.toString();
	}

	protected Transition getTerminateTransition() {
		return getTerminateTransition(null);
	}

	protected Transition getTerminateTransition(String lastTaskName) {
		boolean terminate = false;
		TaskTrigger taskTrigger = getTaskExecuteStrategy().getTrigger();
		if (taskTrigger != null) {
			terminate = taskTrigger.isEndTask();
		} else {
			terminate = getTaskExecuteStrategy().isEndTask();
		}
		if (terminate) {
			return new Transition(true, null, lastTaskName);
		}
		return null;
	}
}
