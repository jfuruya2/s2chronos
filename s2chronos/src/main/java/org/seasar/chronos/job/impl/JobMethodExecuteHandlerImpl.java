package org.seasar.chronos.job.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.chronos.delegate.AsyncResult;
import org.seasar.chronos.delegate.MethodInvoker;

public class JobMethodExecuteHandlerImpl extends AbstractTaskExecuteHandler {

	@Override
	public boolean handleRequest(String startTaskName) throws Throwable {
		MethodInvoker mi = this.getMethodInvoker();
		List<AsyncResult> asyncResultList = new ArrayList<AsyncResult>();
		// �A���ŃW���u���Ăяo��
		String nextMethodName = startTaskName;
		while (true) {
			JobMethodMetaData md = new JobMethodMetaData(mi
					.getMethod(nextMethodName));

			for (int i = 0; i < md.getCloneSize(); i++) {
				AsyncResult ar = mi.beginInvoke(nextMethodName);
				asyncResultList.add(ar);
			}

			Object returnValue = null;
			String nextTaskName = null;
			if (md.getJoinType() == JoinType.Wait) {
				for (AsyncResult ar : asyncResultList) {
					returnValue = mi.endInvoke(ar);
				}
				// �����̏ꍇ�Ŗ߂�l��String�ŃW���u����Ԃ����ꍇ�͑J�ڐ���㏑��
				if (returnValue instanceof String) {
					nextTaskName = (String) returnValue;
				}
			}
			nextMethodName = nextTaskName != null ? nextTaskName : md
					.getNextTask();
			if (nextMethodName == null) {
				break;
			} else if (this.getMethodGroupMap().existGroup(nextMethodName)) {
				return false;
			}
		}

		for (AsyncResult ar : asyncResultList) {
			mi.endInvoke(ar);
		}
		return true;

	}

}
