package org.seasar.chronos.handler.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.seasar.chronos.Scheduler;
import org.seasar.chronos.impl.TaskContena;
import org.seasar.chronos.impl.TaskStateType;
import org.seasar.chronos.impl.TaskContenaStateManager.TaskContenaHanlder;
import org.seasar.chronos.task.TaskExecutorService;
import org.seasar.chronos.util.TaskPropertyUtil;
import org.seasar.framework.container.S2Container;

public class ScheduleExecuteStartHandler extends AbstractScheduleExecuteHandler {

	private S2Container s2container;

	public void setS2Container(S2Container s2container) {
		this.s2container = s2container;
	}

	private Scheduler scheduler;

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void handleRequest() throws InterruptedException {

		this.taskContenaStateManager.forEach(TaskStateType.SCHEDULED,
				new TaskContenaHanlder() {
					public Object processTaskContena(
							final TaskContena taskContena) {
						final TaskExecutorService tes = taskContena
								.getTaskExecutorService();
						log.debug("check task : "
								+ TaskPropertyUtil.getTaskName(tes));
						if (TaskPropertyUtil.getStartTask(tes)) {
							log.debug("start task : "
									+ TaskPropertyUtil.getTaskName(tes));
							// �^�X�N�̊J�n
							log.log("DCHRONOSSSTHRT001",
									new Object[] { TaskPropertyUtil
											.getTaskName(tes) });
							Future<TaskExecutorService> taskStaterFuture = executorService
									.submit(new Callable<TaskExecutorService>() {
										public TaskExecutorService call()
												throws Exception {
											TaskExecutorService _tes = tes;
											Object[] logArgs = new Object[] { TaskPropertyUtil
													.getTaskName(_tes) };
											log.log("DCHRONOS000111", logArgs);
											String nextTaskName = _tes
													.initialize();
											if (nextTaskName != null) {
												try {
													_tes.execute(nextTaskName);
													_tes.waitOne();
												} catch (RejectedExecutionException ex) {
													log.log("ECHRONOS0002",
															logArgs, ex);
												}
											}
											nextTaskName = null;
											nextTaskName = _tes.destroy();
											if (nextTaskName != null) {
												Scheduler scheduler = _tes
														.getScheduler();
												scheduler.addTask(nextTaskName);
											}
											taskContenaStateManager
													.removeTaskContena(
															TaskStateType.RUNNING,
															taskContena);
											log.log("DCHRONOS000112", logArgs);
											return tes;
										}
									});

							taskContena.setTaskStaterFuture(taskStaterFuture);
							taskContenaStateManager.addTaskContena(
									TaskStateType.RUNNING, taskContena);
							taskContenaStateManager.removeTaskContena(
									TaskStateType.SCHEDULED, taskContena);
						}
						return new Object();
					}
				});
	}

}
