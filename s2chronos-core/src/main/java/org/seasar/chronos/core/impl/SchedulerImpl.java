package org.seasar.chronos.core.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.seasar.chronos.core.SchedulerConfiguration;
import org.seasar.chronos.core.SchedulerEventListener;
import org.seasar.chronos.core.TaskScheduleEntry;
import org.seasar.chronos.core.event.SchedulerEventHandler;
import org.seasar.chronos.core.exception.CancellationRuntimeException;
import org.seasar.chronos.core.exception.ExecutionRuntimeException;
import org.seasar.chronos.core.exception.InterruptedRuntimeException;
import org.seasar.chronos.core.handler.ScheduleExecuteHandler;
import org.seasar.chronos.core.schedule.TaskScheduleEntryManager;
import org.seasar.chronos.core.schedule.TaskScheduleEntryManager.TaskScheduleEntryHanlder;
import org.seasar.chronos.core.util.TaskPropertyUtil;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;

public class SchedulerImpl extends AbstractScheduler {

	public static final int SHUTDOWN_AWAIT_TIME = 10;

	public static final TimeUnit SHUTDOWN_AWAIT_TIMEUNIT = TimeUnit.MILLISECONDS;

	private static final SchedulerConfiguration defaultConfiguration = new SchedulerConfiguration();

	private SchedulerEventHandler schedulerEventHandler = new SchedulerEventHandler(
			this);

	private AtomicBoolean pause = new AtomicBoolean();

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Future<Void> schedulerTaskFuture;

	private TaskScheduleEntryManager taskScheduleEntryManager = TaskScheduleEntryManager
			.getInstance();

	private SchedulerConfiguration configuration = defaultConfiguration;

	private ScheduleExecuteHandler scheduleExecuteWaitHandler;

	private ScheduleExecuteHandler scheduleExecuteStartHandler;

	private ScheduleExecuteHandler scheduleExecuteShutdownHandler;

	private long finishStartTime = 0;

	public boolean addListener(SchedulerEventListener listener) {
		return schedulerEventHandler.add(listener);
	}

	/**
	 * タスクを追加します．
	 */
	public synchronized boolean addTask(Class<?> taskComponentClass) {
		S2Container rootS2Container = this.s2container.getRoot();
		boolean result = false;
		result = this.registChildTaskComponentByTarget(rootS2Container,
				taskComponentClass);
		result = result
				|| this.registTaskFromS2ContainerOnSmartDeployByTarget(
						rootS2Container, taskComponentClass);
		return result;
	}

	public SchedulerConfiguration getSchedulerConfiguration() {
		return configuration;
	}

	private boolean getSchedulerFinish() {
		if (finishStartTime != 0
				&& taskScheduleEntryManager.size(TaskStateType.SCHEDULED) > 0) {
			finishStartTime = 0;
			return false;
		}
		if (finishStartTime != 0
				&& (System.currentTimeMillis() - finishStartTime) >= configuration
						.getZeroScheduleTime()) {
			finishStartTime = 0;
			return true;
		}
		if (this.schedulerTaskFuture != null
				&& taskScheduleEntryManager.size(TaskStateType.SCHEDULED) == 0
				&& taskScheduleEntryManager.size(TaskStateType.RUNNING) == 0
				&& configuration.isAutoFinish() && finishStartTime == 0) {
			finishStartTime = System.currentTimeMillis();
		}
		return false;
	}

	public boolean isPaused() {
		return pause.get();
	}

	public void join() {
		log.log("DCHRONOS0016", null);
		try {
			this.schedulerTaskFuture.get();
		} catch (CancellationException e) {
			throw new CancellationRuntimeException(e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof InterruptedException) {
				throw new InterruptedRuntimeException(e.getCause());
			}
			throw new ExecutionRuntimeException(e);
		} catch (InterruptedException e) {
			throw new InterruptedRuntimeException(e);
		} finally {
			if (!this.schedulerTaskFuture.isCancelled()) {
				this.schedulerEventHandler.fireEndScheduler();
			}
		}
		log.log("DCHRONOS0017", null);
	}

	/**
	 * スケジューラを一時停止させます．
	 * 
	 */
	public synchronized void pause() {
		log.log("DCHRONOS0018", null);
		this.pause.set(!this.pause.get());
		this.notify();
	}

	/**
	 * S2コンテナ上のコンポーネントを検索し，スケジューラに登録します．
	 * 
	 */
	protected void registTaskFromS2Container() {
		final S2Container target = this.s2container.getRoot();
		this.registChildTaskComponent(target);
		this.registTaskFromS2ContainerOnSmartDeploy(target);
	}

	/**
	 * リスナーを削除します．
	 * 
	 */
	public boolean removeListener(SchedulerEventListener listener) {
		return schedulerEventHandler.remove(listener);
	}

	/**
	 * タスクを削除します．
	 */
	public synchronized boolean removeTask(Class<?> taskClass) {
		TaskScheduleEntry taskScheduleEntry = this.taskScheduleEntryManager
				.getTaskScheduleEntry(taskClass);
		return this.taskScheduleEntryManager
				.removeTaskScheduleEntry(taskScheduleEntry);
	}

	protected TaskScheduleEntry unscheduleTask(final ComponentDef componentDef) {
		TaskScheduleEntry target = (TaskScheduleEntry) this.taskScheduleEntryManager
				.forEach(new TaskScheduleEntryHanlder() {
					public Object processTaskScheduleEntry(
							TaskScheduleEntry scheduleEntry) {
						if (componentDef
								.equals(scheduleEntry.getComponentDef())) {
							return scheduleEntry;
						}
						return null;
					}
				});
		if (this.taskScheduleEntryManager.removeTaskScheduleEntry(target)) {
			return target;
		}
		return null;
	}

	protected TaskScheduleEntry scheduleTask(ComponentDef taskComponentDef) {
		return scheduleTask(taskComponentDef, false);
	}

	protected TaskScheduleEntry scheduleTask(ComponentDef taskComponentDef,
			boolean force) {
		TaskScheduleEntry taskScheduleEntry = super.scheduleTask(
				taskComponentDef, force);
		if (taskScheduleEntry == null) {
			return null;
		}
		this.taskScheduleEntryManager.addTaskScheduleEntry(
				TaskStateType.SCHEDULED, taskScheduleEntry);
		this.schedulerEventHandler.fireAddTaskScheduleEntry(taskScheduleEntry);
		return taskScheduleEntry;
	}

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

	public void setSchedulerConfiguration(
			SchedulerConfiguration schedulerConfiguration) {
		this.configuration = schedulerConfiguration;
	}

	/**
	 * スケジューラ実行ハンドラーをセットアップします．
	 * 
	 * @return スケジューラ実行ハンドラーの配列
	 */
	private ScheduleExecuteHandler[] setupHandler() {
		ScheduleExecuteHandler[] scheduleExecuteHandlers = new ScheduleExecuteHandler[] {
				scheduleExecuteWaitHandler, scheduleExecuteStartHandler,
				scheduleExecuteShutdownHandler };

		this.scheduleExecuteWaitHandler
				.setExecutorService(this.executorService);
		this.scheduleExecuteWaitHandler.setPause(this.pause);

		this.scheduleExecuteStartHandler
				.setExecutorService(this.executorService);
		this.scheduleExecuteStartHandler
				.setSchedulerEventHandler(this.schedulerEventHandler);

		this.scheduleExecuteShutdownHandler
				.setExecutorService(this.executorService);
		this.scheduleExecuteShutdownHandler
				.setSchedulerEventHandler(this.schedulerEventHandler);

		return scheduleExecuteHandlers;
	}

	public void shutdown() {
		log.log("DCHRONOS0013", null);
		this.taskScheduleEntryManager.forEach(TaskStateType.RUNNING,
				new TaskScheduleEntryManager.TaskScheduleEntryHanlder() {
					public Object processTaskScheduleEntry(
							TaskScheduleEntry taskScheduleEntry) {
						taskScheduleEntry.getTaskExecutorService().cancel();
						try {
							while (!taskScheduleEntry.getTaskExecutorService()
									.await(SHUTDOWN_AWAIT_TIME,
											SHUTDOWN_AWAIT_TIMEUNIT)) {
								String taskName = TaskPropertyUtil
										.getTaskName(taskScheduleEntry
												.getTaskExecutorService());
								log.log("DCHRONOS0014",
										new Object[] { taskName });
							}
						} catch (InterruptedException e) {
							throw new InterruptedRuntimeException(e);
						}
						return null;
					}
				});
		schedulerTaskFuture.cancel(true);
		this.schedulerEventHandler.fireShutdownScheduler();
		log.log("DCHRONOS0015", null);
	}

	public void start() {
		log.log("DCHRONOS0011", null);
		this.schedulerEventHandler.fireRegistTaskBeforeScheduler();
		this.registTaskFromS2Container();
		final ScheduleExecuteHandler[] scheduleExecuteHandlers = this
				.setupHandler();
		this.schedulerEventHandler.fireRegistTaskAfterScheduler();

		this.schedulerTaskFuture = this.executorService
				.submit(new Callable<Void>() {
					public Void call() throws InterruptedException {
						do {
							for (ScheduleExecuteHandler seh : scheduleExecuteHandlers) {
								seh.handleRequest();
								Thread.sleep(configuration
										.getTaskScanIntervalTime());

							}
						} while (!getSchedulerFinish());
						return null;
					}
				});
		this.schedulerEventHandler.fireStartScheduler();
		log.log("DCHRONOS0012", null);
	}

}
