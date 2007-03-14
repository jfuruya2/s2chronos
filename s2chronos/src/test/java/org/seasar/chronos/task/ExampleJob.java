package org.seasar.chronos.task;

import java.util.concurrent.TimeUnit;

import org.seasar.chronos.ThreadPoolType;
import org.seasar.chronos.annotation.job.Job;
import org.seasar.chronos.annotation.job.method.Join;
import org.seasar.chronos.annotation.job.method.NextTask;
import org.seasar.chronos.annotation.job.method.TaskGroup;
import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.framework.log.Logger;

@Job
public class ExampleJob {

	private Logger log = Logger.getLogger(ExampleJob.class);

	// �W���u���\�b�h���ǂ̃^�C�v�̃X���b�h�v�[���Ŏ��s���邩�Ԃ��܂�
	public ThreadPoolType getThreadPoolType() {
		return ThreadPoolType.FIXED;
	}

	// FIXED�̏ꍇ�́A�v�[���T�C�Y��Ԃ��܂��B
	// ex)2�Ƃ����ꍇ������2�X���b�h�܂ł̑��d�x���w��\�B
	public int getThreadPoolSize() {
		return 2;
	}

	// �G���g�����\�b�h
	// �ŏ��Ɏ��s����W���u�������̓W���u�O���[�v���w�肵�܂��B
	@NextTask("groupA")
	public void initialize() {
		log.info("initialize");
	}

	// ------------------- JOB GROUP A
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	// @Next("jobA")
	// public void startGroupA() {
	// log.info("startGroupA");
	// }

	@TaskGroup("groupA")
	@NextTask("jobA")
	public void doHoge() {
		log.info("doHoge");
	}

	// �W���u���\�b�hA
	@TaskGroup("groupA")
	@NextTask("jobB")
	@Join(JoinType.NoWait)
	public void doJobA() throws Exception {

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}

		// throw new Exception();

	}

	// �W���u���\�b�hB
	@TaskGroup("groupA")
	@Join(JoinType.NoWait)
	public void doJobB() {
		for (int i = 1; i < 5; i++) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				break;
			}
			log.info("doJobB");
		}
	}

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	// @Next("groupB")
	public void endGroupA() {
		log.info("endGroupA");
	}

	// �W���u���\�b�hC
	// @TaskGroup("groupB")
	@NextTask("groupB")
	public void doJobC() {
		log.info("doJobC");
	}

	// ------------------- JOB GROUP B
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@NextTask("jobD")
	public void startGroupB() {
		log.info("startGroupB");
	}

	// �W���u���\�b�hD
	@TaskGroup("groupB")
	public void doJobD() {
		log.info("doJobD");
	}

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@NextTask("jobE")
	public void endGroupB() {
		log.info("endGroupB");
	}

	// ------------------- JOB E
	// �W���uE
	@NextTask("jobF")
	public void doJobE() {
		log.info("doJobE");
	}

	// ------------------- JOB F
	// �W���uF
	public void doJobF() {
		log.info("doJobF");
	}

	// �W���u���j�������Ƃ��ɌĂ΂�܂�
	public void destroy() {
		log.info("destroy");
	}

	// �W���u�ŗ�O���X���[�����ƌĂ΂�܂�
	public void cancel() {
		log.info("cancel");
	}

	private boolean executed = false;

	public boolean isExecuted() {
		return executed;
	}

	// ���s�\����Ԃ�
	public boolean canExecute() {
		if (!executed) {
			executed = true;
			return true;
		}
		return false;
	}

	public boolean canTerminate() {
		if (executed) {
			executed = false;
			return true;
		}
		return false;
	}

	public void startScheduler() {
		log.info("startScheduler");
	}

	public void shutdownScheduler() {
		log.info("shutdownScheduler");
	}

}
