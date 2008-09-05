package org.seasar.chronos.sastruts.example.task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.trigger.CronTrigger;
import org.seasar.chronos.sastruts.example.entity.User;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.log.Logger;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * ����̃��[�U�ꗗ��CSV�ŏo�͂���B
 */
@Task
@CronTrigger(expression = "0 */5 * * * ?")
public class ExportActiveMemberTask {

	/** Logger */
	private final Logger log = Logger.getLogger(EventExecuteTask.class);

	/** JdbcManager */
	public JdbcManager jdbcManager;

	/**
	 * ���������\�b�h�ł��B
	 */
	public void initialize() {

	}

	/**
	 * �J�n���\�b�h�ł��B
	 */
	@NextTask("exportCsv")
	public void start() {

	}

	public void doExportCsv() throws IOException {
		List<User> userList = jdbcManager.from(User.class).where(
				"USER_STATUS  != ?", User.STATUS_DISABLE).getResultList();
		if (userList.size() > 0) {
			CSVWriter writer = new CSVWriter(new FileWriter(
					"C:/temp/output.csv"));
			try {
				for (User user : userList) {
					String[] arg = { user.userId.toString(), user.userName,
							user.email };
					writer.writeNext(arg);
				}
			} finally {
				writer.close();
			}
		}
	}

	/**
	 * �I�����\�b�h�ł��B
	 */
	public void finish() {

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
