package org.seasar.chronos.examples.task;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.task.method.TaskGroup;
import org.seasar.chronos.core.annotation.trigger.NonDelayTrigger;
import org.seasar.framework.log.Logger;

/**
 * タスクグループに対応したタスクです．
 * <p>
 * トリガーはNonDelayTriggerです．
 * </p>
 */
@Task
@NonDelayTrigger
public class TaskGroupTask {

	/**
	 * ロガーです．
	 */
	private Logger log = Logger.getLogger(TaskGroupTask.class);

	/**
	 * 初期化メソッドです．
	 */
	@NextTask("groupA")
	public void initialize() {
		log.info("initialize");
	}

	/**
	 * グループ開始メソッドです．
	 */
	@NextTask("taskA")
	public void startGroupA() {
		log.info("startGroupA");
	}

	/**
	 * タスクAメソッドです．
	 */
	@TaskGroup("groupA")
	@NextTask("taskB")
	public void doTaskA() {
		log.info("doTaskA");
	}

	/**
	 * タスクBメソッドです．
	 */
	@TaskGroup("groupA")
	public void doTaskB() {
		log.info("doTaskB");
	}

	/**
	 * グループ終了メソッドです．
	 */
	public void endGroupA() {
		log.info("endGroupA");
	}

	/**
	 * 破棄メソッドです．
	 */
	public void destroy() {
		log.info("destroy");
	}

}
