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
package org.seasar.chronos.core.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seasar.chronos.core.task.handler.TaskPropertyHandler;
import org.seasar.chronos.core.task.handler.factory.TaskPropertyHandlerFactory;

public class TriggerPropertyReadInterceptor implements MethodInterceptor {

	private TaskPropertyHandlerFactory taskPropertyHandlerFactory;

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		TaskPropertyHandler cmd = taskPropertyHandlerFactory
				.create(methodInvocation.getMethod());
		if (cmd != null) {
			return cmd.execute(methodInvocation);
		}
		return methodInvocation.proceed();
	}

	public void setTaskPropertyHandlerFactory(
			TaskPropertyHandlerFactory taskPropertyHandlerFactory) {
		this.taskPropertyHandlerFactory = taskPropertyHandlerFactory;
	}

	public TaskPropertyHandlerFactory getTaskPropertyHandlerFactory() {
		return taskPropertyHandlerFactory;
	}

}
