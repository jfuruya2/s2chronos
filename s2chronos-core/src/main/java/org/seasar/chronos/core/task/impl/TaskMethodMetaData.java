package org.seasar.chronos.core.task.impl;

import java.lang.reflect.Method;

import org.seasar.chronos.core.annotation.task.method.CloneTask;
import org.seasar.chronos.core.annotation.task.method.JoinTask;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.task.method.TaskGroup;
import org.seasar.chronos.core.annotation.type.JoinType;
import org.seasar.framework.beans.BeanDesc;

public class TaskMethodMetaData {

	private static final long serialVersionUID = -9051823745597260320L;

	private Method method;

	public TaskMethodMetaData(BeanDesc beanDesc, String methodName) {
		this(beanDesc.getMethod(methodName));
	}

	public TaskMethodMetaData(Method method) {
		this.method = method;
	}

	public Class<?> getReturnType() {
		return this.method.getReturnType();
	}

	public JoinType getJoinType() {
		JoinTask joinTask = method.getAnnotation(JoinTask.class);
		if (joinTask != null) {
			return joinTask.value();
		}
		return JoinType.Wait;
	}

	public String getNextTask() {
		NextTask nextTask = method.getAnnotation(NextTask.class);
		if (nextTask != null) {
			return nextTask.value();
		}
		return null;
	}

	public long getCloneSize() {
		CloneTask cloneTask = method.getAnnotation(CloneTask.class);
		if (cloneTask != null) {
			return cloneTask.value();
		}
		return 1;
	}

	public String getGroupName() {
		TaskGroup taskGroup = method.getAnnotation(TaskGroup.class);
		if (taskGroup != null) {
			return taskGroup.value();
		}
		return null;
	}
}