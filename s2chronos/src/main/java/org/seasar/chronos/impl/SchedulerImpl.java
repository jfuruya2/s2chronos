package org.seasar.chronos.impl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.seasar.chronos.Scheduler;
import org.seasar.chronos.SchedulerConfiguration;
import org.seasar.chronos.SchedulerEventListener;
import org.seasar.chronos.annotation.task.Task;
import org.seasar.chronos.handler.ScheduleExecuteHandler;
import org.seasar.chronos.util.TaskPropertyUtil;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.util.Traversal;
import org.seasar.framework.log.Logger;

public class SchedulerImpl implements Scheduler {

	private static Logger log = Logger.getLogger(SchedulerImpl.class);

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Future<Void> schedulerTaskFuture;

	private TaskContenaStateManager taskContenaStateManager = TaskContenaStateManager
			.getInstance();

	private S2Container s2container;

	private SchedulerConfiguration configuration = defaultConfiguration;

	private static final SchedulerConfiguration defaultConfiguration = new SchedulerConfiguration();

	public void setS2Container(S2Container s2container) {
		this.s2container = s2container;
	}

	private ScheduleExecuteHandler scheduleExecuteWaitHandler;

	private ScheduleExecuteHandler scheduleExecuteStartHandler;

	private ScheduleExecuteHandler scheduleExecuteShutdownHandler;

	private AtomicBoolean pause = new AtomicBoolean();

	public void setScheduleExecuteShutdownHandler(
			ScheduleExecuteHandler sheduleExecuteShutdownHandler) {
		this.scheduleExecuteShutdownHandler = sheduleExecuteShutdownHandler;
	}

	public void setScheduleExecuteStartHandler(
			ScheduleExecuteHandler sheduleExecuteStartHandler) {
		this.scheduleExecuteStartHandler = sheduleExecuteStartHandler;
	}

	public void setScheduleExecuteWaitHandler(
			ScheduleExecuteHandler sheduleExecuteWaitHandler) {
		this.scheduleExecuteWaitHandler = sheduleExecuteWaitHandler;
	}

	public void addListener(SchedulerEventListener listener) {

	}

	public void removeListener(SchedulerEventListener listener) {

	}

	public SchedulerConfiguration getSchedulerConfiguration() {
		return configuration;
	}

	public void join() throws InterruptedException {
		try {
			this.schedulerTaskFuture.get();
		} catch (CancellationException e) {
			log.debug(e);
			while (!schedulerTaskFuture.isDone()) {
				log.debug("キャンセル待機中");
			}
			log.debug("キャンセルチェック完了");
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		pause.set(!pause.get());
		this.notify();
	}

	public void setSchedulerConfiguration(
			SchedulerConfiguration schedulerConfiguration) {
		this.configuration = schedulerConfiguration;
	}

	public void shutdown() throws InterruptedException {
		// キャンセルしたタスクが残っていれば
		final List<TaskContena> runningTaskList = taskContenaStateManager
				.getTaskContenaList(TaskStateType.RUNNING);
		for (TaskContena tc : runningTaskList) {
			tc.getTaskExecutorService().cancel();
			while (!tc.getTaskExecutorService()
					.await(10, TimeUnit.MILLISECONDS)) {
				String taskName = TaskPropertyUtil.getTaskName(tc
						.getTaskExecutorService());
				log.debug("Task (" + taskName + ") のShutdown 待機中");
			}
		}
		schedulerTaskFuture.cancel(true);
	}

	private boolean getSchedulerFinish() {
		final List<TaskContena> runingTaskList = taskContenaStateManager
				.getTaskContenaList(TaskStateType.RUNNING);
		if (this.schedulerTaskFuture != null && runingTaskList.size() == 0
				&& configuration.isAutoFinish()) {
			return true;
		}
		return false;
	}

	public void start() {
		this.registTaskFromS2Container();
		this.setupHandler();
		this.schedulerTaskFuture = this.executorService
				.submit(new Callable<Void>() {
					public Void call() throws Exception {
						do {
							scheduleExecuteWaitHandler.handleRequest();
							scheduleExecuteStartHandler.handleRequest();
							scheduleExecuteShutdownHandler.handleRequest();
						} while (!getSchedulerFinish());
						return null;
					}
				});
	}

	private void setupHandler() {
		this.scheduleExecuteWaitHandler
				.setExecutorService(this.executorService);
		this.scheduleExecuteWaitHandler.setPause(this.pause);
		this.scheduleExecuteStartHandler
				.setExecutorService(this.executorService);
		this.scheduleExecuteShutdownHandler
				.setExecutorService(this.executorService);
	}

	private void registChildTaskComponent(S2Container targetContainer) {
		Traversal.forEachComponent(targetContainer,
				new Traversal.ComponentDefHandler() {
					public Object processComponent(ComponentDef componentDef) {
						Class clazz = componentDef.getComponentClass();
						Task task = (Task) clazz.getAnnotation(Task.class);
						if (task != null) {
							CopyOnWriteArrayList<TaskContena> list = taskContenaStateManager
									.getTaskContenaList(TaskStateType.SCHEDULED);
							list.addIfAbsent(new TaskContena(componentDef));
						}
						return null;
					}
				});
	}

	private void registTaskFromS2Container() {
		S2Container target = this.s2container.getRoot();
		this.registChildTaskComponent(target);
	}

	public boolean addTask(Object task) {
		final CopyOnWriteArrayList<TaskContena> taskList = taskContenaStateManager
				.getTaskContenaList(TaskStateType.SCHEDULED);
		TaskContena tc = new TaskContena();
		tc.setTarget(task);
		tc.setTargetClass(task.getClass());
		boolean result = taskList.add(tc);
		synchronized (this) {
			this.notify();
		}
		return result;
	}

	public boolean removeTask(Object task) {
		final CopyOnWriteArrayList<TaskContena> taskList = taskContenaStateManager
				.getTaskContenaList(TaskStateType.SCHEDULED);
		for (TaskContena tc : taskList) {
			if (tc.getTarget() == task) {
				return taskList.remove(tc);
			}
		}
		return false;
	}

}
