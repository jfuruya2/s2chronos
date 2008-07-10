package org.seasar.chronos.teeda.example.task;

import java.sql.Timestamp;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.trigger.CronTrigger;
import org.seasar.chronos.teeda.example.dao.S2SessionDao;

@Task
@CronTrigger(expression = "0 */1 * * * ?")
public class SessionTimerTask {

	public S2SessionDao s2SessionDao;

	@NextTask("expire")
	public void initialize() {

	}

	public void doExpire() {
		s2SessionDao.deleteOldSession(new Timestamp(
				System.currentTimeMillis() - 36000));
	}

	public void destroy() {

	}

}
