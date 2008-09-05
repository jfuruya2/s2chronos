package org.seasar.chronos.sastruts.example.task;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.trigger.CronTrigger;
import org.seasar.chronos.sastruts.example.dto.MailDto;
import org.seasar.chronos.sastruts.example.entity.User;
import org.seasar.chronos.sastruts.example.mai.RegisterMai;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.tiger.CollectionsUtil;

/**
 * ���[�U�Ƀ��[����ʒm����^�X�N�N���X�ł��B
 */
@Task
@CronTrigger(expression = "0 */1 * * * ?")
public class MemberMailNoticeTask {

	/** Logger */
	private final Logger log = Logger.getLogger(MemberMailNoticeTask.class);

	/** jdbcManager */
	public JdbcManager jdbcManager;

	/** registerMai */
	public RegisterMai regsiterMai;

	/** ���[�U�o�^�̍ŏI�X�V���� */
	private Timestamp registerLastUpdateDate;

	/** ���[�U�މ�̍ŏI�X�V���� */
	private Timestamp unregisterLastUpdateDate;

	/** ��������[�U�ꗗ */
	private List<User> registerUserList = CollectionsUtil.newArrayList();

	/** �މ�����[�U�ꗗ */
	private List<User> unregisterUserList = CollectionsUtil.newArrayList();

	/**
	 * ���������\�b�h�ł��B
	 */
	public void initialize() {

	}

	/**
	 * �J�n���\�b�h�ł��B
	 */
	@NextTask("registerMail")
	public void start() {
		buildRegisterUserList();
		buildUnregisterUserList();
	}

	/**
	 * ��������[�U�ꗗ���쐬���܂��B
	 */
	private void buildRegisterUserList() {
		BeanMap beanMap = jdbcManager
				.selectBySql(
						BeanMap.class,
						"SELECT MAX(UPDATE_DATE) AS LAST_UPDATE_DATE FROM USER�@WHERE�@USER_STATUS != ?",
						User.STATUS_DISABLE).getSingleResult();
		registerLastUpdateDate = (Timestamp) beanMap.get("lastUpdateDate");
		if (registerLastUpdateDate != null) {
			registerUserList = jdbcManager.from(User.class).where(
					"UPDATE_DATE >= ? AND USER_STATUS != ?",
					registerLastUpdateDate, User.STATUS_DISABLE)
					.getResultList();
		}
	}

	/**
	 * �މ�����[�U�ꗗ���쐬���܂��B
	 */
	private void buildUnregisterUserList() {
		BeanMap beanMap = jdbcManager
				.selectBySql(
						BeanMap.class,
						"SELECT MAX(UPDATE_DATE) AS LAST_UPDATE_DATE FROM USER�@WHERE�@USER_STATUS = ?",
						User.STATUS_DISABLE).getSingleResult();
		unregisterLastUpdateDate = (Timestamp) beanMap.get("lastUpdateDate");
		if (unregisterLastUpdateDate != null) {
			unregisterUserList = jdbcManager.from(User.class).where(
					"UPDATE_DATE >= ? AND USER_STATUS = ?",
					registerLastUpdateDate, User.STATUS_DISABLE)
					.getResultList();
		}
	}

	/**
	 * ��������[�U�Ƀ��[���𑗐M���܂��B
	 */
	@NextTask("unregisterMail")
	public void doRegisterMail() {
		for (User user : registerUserList) {
			MailDto dto = new MailDto();
			dto.setFrom("kato@globalspace-it.com");
			dto.setTo(user.email);
			dto.setUserName(user.userName);
			this.regsiterMai.sendRegisterMail(dto);
		}
	}

	/**
	 * �މ�����[�U�Ƀ��[���𑗐M���܂��B
	 */
	public void doUnregisterMail() {
		for (User user : unregisterUserList) {
			MailDto dto = new MailDto();
			dto.setFrom("kato@globalspace-it.com");
			dto.setTo(user.email);
			dto.setUserName(user.userName);
			this.regsiterMai.sendUnregisterMail(dto);
		}
	}

	/**
	 * �I�����\�b�h�ł��B
	 */
	public void finish() {
		registerUserList.clear();
		unregisterUserList.clear();
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
