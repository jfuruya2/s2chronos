package org.seasar.chronos.core.test.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.model.TaskTrigger;
import org.seasar.chronos.core.model.trigger.CCronTrigger;
import org.seasar.chronos.core.model.trigger.ScopedTimeTrigger;

@Task(autoSchedule = false)
public class ScopedTimeTriggerTask {
	private final ScopedTimeTrigger trigger =
		new ScopedTimeTrigger(new CCronTrigger("0 */1 * * * ?"));

	public TaskTrigger getTrigger() {
		return this.trigger;
	}

	public boolean isForceUnScheduleTask() {
		boolean result = trigger.isEndTask();
		return result;
	}

	public void initialize() {
		Date scopedStartTime = null;
		Date scopedEndTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			scopedStartTime = sdf.parse("20080919080000");
			scopedEndTime = sdf.parse("20080919131300");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.trigger.setScopedStartTime(scopedStartTime);
		this.trigger.setScopedEndTime(scopedEndTime);
		this.trigger.setReScheduleTask(true);
	}

	public void doExecute() {
		System.out.println("ScopedTimeTriggerTask#doExecute()が実行されました");
	}

	public void end() {
		System.out.println("ScopedTimeTriggerTask#end()が実行されました");
	}

	public void destroy() {
		System.out.println("ScopedTimeTriggerTask#destroy()が実行されました");
	}
}
