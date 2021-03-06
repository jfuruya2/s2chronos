/*
 * Copyright 2007-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.chronos.core.impl;

import org.seasar.chronos.core.Scheduler;
import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.autodetector.TaskClassAutoDetector;
import org.seasar.chronos.core.model.TaskScheduleEntry;
import org.seasar.chronos.core.model.schedule.TaskScheduleEntryManager;
import org.seasar.chronos.core.task.TaskExecutorService;
import org.seasar.chronos.core.task.TaskValidator;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.util.SmartDeployUtil;
import org.seasar.framework.container.util.Traversal;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ClassTraversal;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * 抽象スケジューラクラスです。
 * 
 * @author j5ik2o
 */
public abstract class AbstractScheduler implements Scheduler {
	protected static final Logger log =
	    Logger.getLogger(AbstractScheduler.class);

	protected S2Container s2container;

	protected TaskClassAutoDetector taskClassAutoDetector;

	private TaskValidator taskValidator;

	public AbstractScheduler() {
		super();
	}

	/**
	 * タスク名からComponentDefを返します．
	 * 
	 * @param taskName
	 *            タスク名
	 * @return ComponentDef
	 */
	protected ComponentDef findTaskComponentDefByTaskName(final String taskName) {
		TaskScheduleEntryManager tcsm = TaskScheduleEntryManager.getInstance();
		ComponentDef componentDef =
		    tcsm
		        .forEach(new TaskScheduleEntryManager.TaskScheduleEntryHanlder<ComponentDef>() {
			        public ComponentDef processTaskScheduleEntry(
			                TaskScheduleEntry taskScheduleEntry) {
				        TaskExecutorService tes =
				            taskScheduleEntry.getTaskExecutorService();
				        String _taskName =
				            tes.getTaskPropertyReader().getTaskName(null);
				        if (taskName.equals(_taskName)) {
					        return taskScheduleEntry.getComponentDef();
				        }
				        return null;
			        }
		        });
		if (componentDef != null) {
			return componentDef;
		}
		return null;
	}

	/**
	 * S2コンテナ(非SMART Deploy)上のコンポーネントを検索し，スケジューラに登録します．
	 * 
	 * @param s2Container
	 *            S2コンテナ
	 */
	protected boolean registerChildTaskComponent(S2Container s2Container) {
		return this.registerChildTaskComponent(s2Container, null);
	}

	protected boolean registerChildTaskComponentByTarget(
	        S2Container s2Container, final Class<?> targetTaskComponentClass) {
		return this.registerChildTaskComponent(
		    s2Container,
		    targetTaskComponentClass);
	}

	private boolean registerChildTaskComponent(S2Container s2Container,
	        final Class<?> targetTaskComponentClass) {
		Object result =
		    Traversal.forEachComponent(
		        s2Container,
		        new Traversal.ComponentDefHandler() {
			        public Object processComponent(ComponentDef componentDef) {
				        Class<?> clazz = componentDef.getComponentClass();
				        if (clazz != null) {
					        if (taskValidator.isValid(clazz)) {
						        if (targetTaskComponentClass == null) {
							        AbstractScheduler.this
							            .scheduleTask(componentDef);
						        } else if (targetTaskComponentClass
						            .equals(componentDef.getComponentClass())) {
							        return AbstractScheduler.this.scheduleTask(
							            componentDef,
							            true);
						        }
					        }
				        }
				        return null;
			        }
		        });
		return result != null;
	}

	protected abstract void registerTaskFromS2Container();

	/**
	 * SMART Deploy上のコンポーネントを検索し，スケジューラに登録します．
	 * 
	 * @param s2Container
	 *            S2コンテナ
	 */
	protected boolean registerTaskFromS2ContainerOnSmartDeploy(
	        final S2Container s2Container) {
		return this.registerTaskFromS2ContainerOnSmartDeploy(s2Container, null);
	}

	protected boolean registerTaskFromS2ContainerOnSmartDeployByTarget(
	        final S2Container s2Container,
	        final Class<?> targetTaskComponentClass) {
		return this.registerTaskFromS2ContainerOnSmartDeploy(
		    s2Container,
		    targetTaskComponentClass);
	}

	protected boolean registerJavascriptTaskComponent() {
		// ReflectionUtil.forNameNoException(className);
		return false;
	}

	private boolean detectResult = false;

	private boolean registerTaskFromS2ContainerOnSmartDeploy(
	        final S2Container s2Container,
	        final Class<?> targetTaskComponentClass) {
		this.detectResult = false;
		if (SmartDeployUtil.isSmartdeployMode(s2Container)) {
			this.taskClassAutoDetector
			    .detect(new ClassTraversal.ClassHandler() {
				    public void processClass(String packageName,
				            String shortClassName) {
					    String name =
					        ClassUtil.concatName(packageName, shortClassName);
					    Class<?> clazz =
					        ReflectionUtil.forNameNoException(name);
					    if (targetTaskComponentClass == null) {
						    if (null != AbstractScheduler.this.scheduleTask(
						        s2Container,
						        clazz)) {
							    AbstractScheduler.this.detectResult = true;
						    }
					    } else if (targetTaskComponentClass.equals(clazz)) {
						    if (null != AbstractScheduler.this.scheduleTask(
						        s2Container,
						        clazz,
						        true)) {
							    AbstractScheduler.this.detectResult = true;
						    }
					    }
				    }
			    });
		}
		return this.detectResult;
	}

	protected TaskScheduleEntry scheduleTask(ComponentDef componentDef) {
		return this.scheduleTask(componentDef, false);
	}

	protected TaskScheduleEntry scheduleTask(ComponentDef taskComponentDef,
	        boolean force) {
		Class<?> clazz = taskComponentDef.getComponentClass();
		Task task = clazz.getAnnotation(Task.class);
		if (!task.autoSchedule() && !force) {
			return null;
		}
		TaskScheduleEntry taskScheduleEntry =
		    (TaskScheduleEntry) this.s2container
		        .getComponent(TaskScheduleEntry.class);
		taskScheduleEntry.setComponentDef(taskComponentDef);
		final TaskExecutorService tes =
		    (TaskExecutorService) this.s2container
		        .getComponent(TaskExecutorService.class);
		taskScheduleEntry.setTaskExecutorService(tes);
		tes.setComponentDef(taskComponentDef);
		tes.setGetterSignal(this);
		tes.setScheduler(this);
		// taskScheduleEntry.setComponentDef(taskComponentDef);
		return taskScheduleEntry;
	}

	protected TaskScheduleEntry scheduleTask(final S2Container s2Container,
	        final Class<?> taskClass, boolean force) {
		ComponentDef componentDef = s2Container.getComponentDef(taskClass);
		return this.scheduleTask(componentDef, force);
	}

	protected TaskScheduleEntry scheduleTask(final S2Container s2Container,
	        final Class<?> taskClass) {
		ComponentDef componentDef = s2Container.getComponentDef(taskClass);
		return this.scheduleTask(componentDef);
	}

	public void setS2Container(S2Container s2container) {
		this.s2container = s2container;
	}

	public void setTaskClassAutoDetector(
	        TaskClassAutoDetector taskClassAutoDetector) {
		this.taskClassAutoDetector = taskClassAutoDetector;
	}

	public void setTaskValidator(TaskValidator taskValidator) {
		this.taskValidator = taskValidator;
	}
}
