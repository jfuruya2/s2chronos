package org.seasar.chronos.core.task;

import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.task.method.TaskGroup;
import org.seasar.chronos.core.trigger.CNonDelayTrigger;
import org.seasar.framework.log.Logger;

@Task
public class TaskGroupTask {

	private Logger log = Logger.getLogger(TaskGroupTask.class);

	public Exception exception;

	public TaskTrigger getTrigger() {
		return new CNonDelayTrigger();
	}

	@NextTask("groupA")
	public void initialize() {
		log.info("initialize");
	}

	@NextTask("taskA")
	public void startGroupA() {
		throw new RuntimeException();
		// log.info("startGroupA");
	}

	@TaskGroup("groupA")
	@NextTask("taskB")
	public void doTaskA() {
		log.info("doTaskA");
	}

	@TaskGroup("groupA")
	public void doTaskB() {
		log.info("doTaskB");
	}

	public void endGroupA() {
		log.info("endGroupA");
	}

	public void destroy() {
		if (exception != null) {
			exception.printStackTrace();
		}
		log.info("destroy");
	}

	// public Exception getException() {
	// return exception;
	// }
	//
	// public void setException(Exception exception) {
	// this.exception = exception;
	// }

}
