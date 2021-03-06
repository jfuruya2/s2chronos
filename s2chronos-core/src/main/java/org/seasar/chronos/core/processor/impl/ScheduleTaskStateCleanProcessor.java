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
package org.seasar.chronos.core.processor.impl;

import org.seasar.chronos.core.model.SchedulerConfiguration;
import org.seasar.chronos.core.model.TaskScheduleEntry;
import org.seasar.chronos.core.model.TaskStateType;
import org.seasar.chronos.core.model.schedule.TaskScheduleEntryManager.TaskScheduleEntryHanlder;
import org.seasar.chronos.core.task.TaskExecutorService;
import org.seasar.chronos.core.task.handler.impl.property.PropertyCache;

/**
 * スケジュールされタスク状態をクリーンナップするハンドラークラスです。
 * 
 * @author j5ik2o
 */
public class ScheduleTaskStateCleanProcessor extends
        AbstractScheduleExecuteProcessor {
	/**
	 * {@link SchedulerConfiguration}です。
	 */
	private SchedulerConfiguration schedulerConfiguration;

	/**
	 * 実行中でなく強制アンスケジュール状態のタスクをスケジュール状態からアンスケジュール状態に遷移させます。
	 * さらに、アンスケジュール状態からタスククリーンナップ時間を経過したタスクをアンスケジュール状態から削除します。
	 * タスクを削除する前に、destroyメソッドをコールします。その後スケジュール情報から削除されます。
	 * 
	 * @see org.seasar.chronos.core.processor.impl.AbstractScheduleExecuteProcessor#
	 *      doProcess()
	 */
	@Override
	public void doProcess() throws InterruptedException {
		this.taskScheduleEntryManager.forEach(
		    TaskStateType.SCHEDULED,
		    new TaskScheduleEntryHanlder<Void>() {
			    public Void processTaskScheduleEntry(
			            TaskScheduleEntry scheduleEntry) {
				    TaskExecutorService tes =
				        scheduleEntry.getTaskExecutorService();
				    // 実行中ではなく強制アンスケジュールが有効であれば
				    if (!tes.getTaskPropertyReader().isExecuting(false)
				        && tes.getTaskPropertyReader().isForceUnScheduleTask(
				            false)) {
					    taskScheduleEntryManager.removeTaskScheduleEntry(
					        TaskStateType.SCHEDULED,
					        scheduleEntry);
					    taskScheduleEntryManager.addTaskScheduleEntry(
					        TaskStateType.UNSCHEDULED,
					        scheduleEntry);
				    }
				    return null;
			    }
		    });
		TaskScheduleEntry taskScheduleEntry =
		    this.taskScheduleEntryManager.forEach(
		        TaskStateType.UNSCHEDULED,
		        new TaskScheduleEntryHanlder<TaskScheduleEntry>() {
			        public TaskScheduleEntry processTaskScheduleEntry(
			                TaskScheduleEntry scheduleEntry) {
				        long now = System.currentTimeMillis();
				        if (scheduleEntry.getUnScheduledDate() != null) {
					        if (now > schedulerConfiguration
					            .getTaskStateCleanupTime()
					            + scheduleEntry.getUnScheduledDate().getTime()) {
						        return scheduleEntry;
					        }
				        }
				        return null;
			        }
		        });
		if (taskScheduleEntry != null) {
			// destroyメソッドを実行します．
			taskScheduleEntry.getTaskExecutorService().destroy();
			taskScheduleEntryManager.removeTaskScheduleEntry(taskScheduleEntry);
			PropertyCache propertyCache =
			    PropertyCache.getInstance(taskScheduleEntry.getTask());
			propertyCache.clear();
			propertyCache = null;
			taskScheduleEntry = null;
		}
	}

	/**
	 * {@link SchedulerConfiguration}を設定します。
	 * 
	 * @param schedulerConfiguration
	 *            {@link SchedulerConfiguration}
	 */
	public void setSchedulerConfiguration(
	        SchedulerConfiguration schedulerConfiguration) {
		this.schedulerConfiguration = schedulerConfiguration;
	}
}
