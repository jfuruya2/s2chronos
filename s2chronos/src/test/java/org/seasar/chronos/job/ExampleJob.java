package org.seasar.chronos.job;

import java.util.concurrent.TimeUnit;

import org.seasar.chronos.ThreadPoolType;
import org.seasar.chronos.annotation.job.Job;
import org.seasar.chronos.annotation.job.method.Group;
import org.seasar.chronos.annotation.job.method.Join;
import org.seasar.chronos.annotation.job.method.Next;
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
	@Next("groupA")
	public void initialize() {
		log.info("initialize");
	}

	// ------------------- JOB GROUP A
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@Next("jobA")
	public void startGroupA() {
		log.info("startGroupA");
	}

	// �W���u���\�b�hA
	@Group("groupA")
	@Next("jobB")
	@Join(JoinType.NoWait)
	public void doJobA() throws Exception {

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}

		throw new Exception();

	}

	// �W���u���\�b�hB
	@Group("groupA")
	@Join(JoinType.NoWait)
	public void doJobB() {
		for (int i = 1; i < 5; i++) {
			try {
				TimeUnit.SECONDS.sleep(20);
			} catch (InterruptedException e) {
				break;
			}
			log.info("doJobB");
		}
	}

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@Next("groupB")
	public void endGroupA() {
		log.info("endGroupA");
	}

	// ------------------- JOB GROUP B
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@Next("jobC")
	public void startGroupB() {
		log.info("startGroupB");
	}

	// �W���u���\�b�hC
	@Group("groupB")
	@Next("jobD")
	public void doJobC() {
		log.info("doJobC");
	}

	// �W���u���\�b�hD
	@Group("groupB")
	public void doJobD() {
		log.info("doJobD");
	}

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@Next("jobE")
	public void endGroupB() {
		log.info("endGroupB");
	}

	// ------------------- JOB E
	// �W���uE
	@Next("jobF")
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

	// ���s�\����Ԃ�
	public boolean canExecute() {
		return true;
	}

	public void startScheduler() {
		log.info("startScheduler");
	}

	public void shutdownScheduler() {
		log.info("shutdownScheduler");
	}

}
