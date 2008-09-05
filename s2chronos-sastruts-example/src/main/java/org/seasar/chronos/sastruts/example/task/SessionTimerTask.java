package org.seasar.chronos.sastruts.example.task;

import java.sql.Timestamp;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.trigger.CronTrigger;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.log.Logger;

/**
 * �Â��Ȃ���S2SESSION�����폜���܂��B
 */
@Task
@CronTrigger(expression = "0 */1 * * * ?")
public class SessionTimerTask {

	/** Logger */
	private final Logger log = Logger.getLogger(SessionTimerTask.class);

	/** JdbcManager */
	public JdbcManager jdbcManager;

	/**
	 * 5���O�̃Z�b�V���������폜���܂��B
	 */
	public void doExecute() {
		jdbcManager.updateBySql("DELETE FROM S2SESSION WHERE LAST_ACCESS < ?",
				Timestamp.class).params(
				new Timestamp(System.currentTimeMillis() - 5 * 60 * 1000))
				.execute();
	}

	/**
	 * ��O���L���b�`���܂��B
	 * 
	 * @param ex
	 *            ��O
	 */
	public void catchException(Exception ex) {
		log.error(ex);
	}

}
