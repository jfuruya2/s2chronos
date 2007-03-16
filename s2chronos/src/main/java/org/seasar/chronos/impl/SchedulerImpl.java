package org.seasar.chronos.impl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.seasar.chronos.Scheduler;
import org.seasar.chronos.SchedulerConfig;
import org.seasar.chronos.SchedulerEventListener;
import org.seasar.chronos.annotation.task.Task;
import org.seasar.chronos.exception.SchedulerException;
import org.seasar.chronos.task.TaskExecutorService;
import org.seasar.chronos.trigger.Trigger;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.util.Traversal;

public class SchedulerImpl implements Scheduler {

	private static final String TASK_TYPE_SCHEDULED = "SCHEDULED";

	private static final String TASK_TYPE_RUNTASK = "RUNTASK";

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Future<Void> future;

	private ConcurrentHashMap<String, CopyOnWriteArrayList<TaskContena>> taskContenaMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<TaskContena>>();

	private List<Future<TaskExecutorService>> futureList = new CopyOnWriteArrayList<Future<TaskExecutorService>>();

	private S2Container s2container;

	public void setS2Container(S2Container s2container) {
		this.s2container = s2container;
	}

	private CopyOnWriteArrayList<TaskContena> getTaskContenaMap(String key) {
		CopyOnWriteArrayList<TaskContena> result = this.taskContenaMap.get(key);
		if (result == null) {
			result = new CopyOnWriteArrayList<TaskContena>();
			this.putMasterTaskContena(key, result);
		}
		return result;
	}

	private void putMasterTaskContena(String key,
			CopyOnWriteArrayList<TaskContena> taskContenaList) {
		this.taskContenaMap.put(key, taskContenaList);
	}

	public void addListener(SchedulerEventListener listener) {

	}

	public SchedulerConfig getConfig() {
		return null;
	}

	public void join() throws InterruptedException {
		try {
			future.get();
		} catch (ExecutionException e) {
			;
		}
	}

	public void pause() throws SchedulerException {

	}

	public void removeListener(SchedulerEventListener listener) {

	}

	public void setConfig(SchedulerConfig config) {

	}

	public void shutdown() throws SchedulerException {

	}

	public void shutdown(boolean waitAllJobFinish) throws SchedulerException {
		executorService.shutdown();
		try {
			executorService.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new SchedulerException(e);
		}
	}

	public void start() throws SchedulerException {

		// �R���e�i����^�X�N����肾��
		this.getTaskFromContainer();

		future = executorService.submit(new Callable<Void>() {

			public Void call() throws Exception {
				while (true) {
					TimeUnit.SECONDS.sleep(1);
					taskStarter();
					taskEnder();
				}
			}

		});
	}

	private void taskEnder() throws InterruptedException {
		CopyOnWriteArrayList<TaskContena> runTaskList = getTaskContenaMap(TASK_TYPE_RUNTASK);
		for (TaskContena tc : runTaskList) {
			final TaskExecutorService tes = (TaskExecutorService) s2container
					.getComponent(TaskExecutorService.class);
			tes.setTaskComponentDef(tc.getComponentDef());
			Trigger trigger = tes.getTrigger();
			if (trigger.getEndTask()) {
				tes.setEndTask(true);
				try {
					tc.getFuture().get();
				} catch (ExecutionException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}

			}
		}
	}

	private void taskStarter() throws InterruptedException {
		CopyOnWriteArrayList<TaskContena> taskList = getTaskContenaMap(TASK_TYPE_SCHEDULED);
		CopyOnWriteArrayList<TaskContena> runTaskList = getTaskContenaMap(TASK_TYPE_RUNTASK);
		for (TaskContena tc : taskList) {
			final TaskExecutorService tes = (TaskExecutorService) s2container
					.getComponent(TaskExecutorService.class);
			tes.setTaskComponentDef(tc.getComponentDef());
			tes.prepare();
			boolean start = false;
			Trigger trigger = tes.getTrigger();
			if (trigger == null) {
				start = tes.getStartTask();
			} else {
				start = trigger.getStartTask();
			}
			if (start) {
				// �^�X�N�̊J�n
				Future<TaskExecutorService> future = executorService
						.submit(new Callable<TaskExecutorService>() {
							public TaskExecutorService call() throws Exception {
								synchronized (tes) {
									tes.notify();
								}
								String nextTaskName = tes.initialize();
								if (nextTaskName != null) {
									tes.execute(nextTaskName);
								}

								tes.destroy();
								return tes;
							}
						});
				synchronized (tes) {
					tes.wait();
				}
				futureList.add(future);
				tc.setFuture(future);
				tc.setTaskExecutorService(tes);
				runTaskList.add(tc);
			}
		}
	}

	private void findComponent(S2Container targetContainer) {
		Traversal.forEachComponent(targetContainer,
				new Traversal.ComponentDefHandler() {
					public Object processComponent(ComponentDef componentDef) {
						Class clazz = componentDef.getComponentClass();
						Task task = (Task) clazz.getAnnotation(Task.class);
						if (task != null) {
							CopyOnWriteArrayList<TaskContena> list = getTaskContenaMap(TASK_TYPE_SCHEDULED);
							list.addIfAbsent(new TaskContena(componentDef));
						}
						return null;
					}
				});
	}

	private void findChildComponent(S2Container targetContainer) {
		findComponent(targetContainer);
		// for (int i = 0; i < targetContainer.getChildSize(); i++) {
		// targetContainer = targetContainer.getChild(i);
		// findChildComponent(targetContainer);
		// }
	}

	private void getTaskFromContainer() {
		S2Container target = this.s2container.getRoot();
		findChildComponent(target);
	}

	public boolean addTask(Object task) {

		return false;
	}

	public boolean removeTask(Object task) {

		return false;
	}

}
