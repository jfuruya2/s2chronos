package org.seasar.chronos.task.handler.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.chronos.delegate.AsyncResult;
import org.seasar.chronos.delegate.MethodInvoker;
import org.seasar.chronos.task.Transition;
import org.seasar.chronos.task.impl.TaskMethodMetaData;

public class TaskMethodExecuteHandlerImpl extends AbstractTaskExecuteHandler {

	@Override
	public Transition handleRequest(String startTaskName)
			throws InterruptedException {
		MethodInvoker mi = this.getMethodInvoker();
		List<AsyncResult> asyncResultList = new ArrayList<AsyncResult>();
		// �A����do���\�b�h���Ăяo��
		String nextTaskName = startTaskName;
		String lastTaskName = startTaskName;
		while (true) {
			if (getTaskExecuteStrategy().getScheduler().addTask(nextTaskName)) {
				break;
			}
			final String methodName = toMethodName(nextTaskName);

			TaskMethodMetaData md = new TaskMethodMetaData(mi
					.getMethod(methodName));

			for (int i = 0; i < md.getCloneSize(); i++) {

				Transition ts = getTerminateTransition(lastTaskName);
				if (ts != null) {
					return ts;
				}

				AsyncResult ar = mi.beginInvoke(methodName);

				asyncResultList.add(ar);
			}

			Object returnValue = null;
			String _nextTaskName = null;
			if (md.getJoinType() == JoinType.Wait
					|| md.getReturnType() == String.class) {
				for (AsyncResult ar : asyncResultList) {
					returnValue = mi.endInvoke(ar);
				}
				if (md.getReturnType() == String.class) {
					_nextTaskName = (String) returnValue;
				}
			}

			nextTaskName = _nextTaskName != null ? _nextTaskName : md
					.getNextTask();

			if (nextTaskName != null) {
				if (!this.getMethodGroupMap().existGroup(nextTaskName)) {
					TaskMethodMetaData nextMethodMetaData = new TaskMethodMetaData(
							mi.getMethod(toMethodName(nextTaskName)));
					String nextGroupName = nextMethodMetaData.getGroupName();
					String currentGroupName = md.getGroupName();
					// �O���[�v�����ύX�ɂȂ�����
					if (nextGroupName != null
							&& !nextGroupName.equals(currentGroupName)) {
						nextTaskName = nextGroupName;
					}
				}
			}

			if (nextTaskName == null) {
				break;
			} else if (this.getMethodGroupMap().existGroup(nextTaskName)) {
				return new Transition(false, nextTaskName, lastTaskName);
			}
			lastTaskName = nextTaskName;
		}

		for (AsyncResult ar : asyncResultList) {
			mi.endInvoke(ar);
		}
		return new Transition(true, null, lastTaskName);

	}

}
