package org.seasar.chronos.examples.task;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.task.method.TaskGroup;
import org.seasar.chronos.core.annotation.trigger.NonDelayTrigger;
import org.seasar.framework.log.Logger;

/**
 * 
 * 
 */
@Task(autoSchedule = true)
@NonDelayTrigger
public class TaskGroupTask {

	private Logger log = Logger.getLogger(TaskGroupTask.class);

	@NextTask("groupA")
	public void initialize() {
		log.info("initialize");
	}

	@NextTask("taskA")
	public void startGroupA() {
		log.info("startGroupA");
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
		log.info("destroy");
	}

}