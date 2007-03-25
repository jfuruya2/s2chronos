package org.seasar.chronos.task;

import java.util.concurrent.TimeUnit;

import org.seasar.chronos.ThreadPoolType;
import org.seasar.chronos.annotation.task.Task;
import org.seasar.chronos.annotation.task.method.JoinTask;
import org.seasar.chronos.annotation.task.method.NextTask;
import org.seasar.chronos.annotation.task.method.TaskGroup;
import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.framework.log.Logger;

@Task(name = "example")
public class ExampleTask {

	private Logger log = Logger.getLogger(ExampleTask.class);

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

	// ------------------- Task GROUP A
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@NextTask("taskA")
	public void startGroupA() {
		log.info("startGroupA");
	}

	@TaskGroup("groupA")
	@NextTask("taskA")
	public void doHoge() {
		log.info("doHoge");
	}

	// �W���u���\�b�hA
	@TaskGroup("groupA")
	@NextTask("taskB")
	@JoinTask(JoinType.NoWait)
	public void doTaskA() throws Exception {

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			log.info("doTaskA��InterruptedException���������܂����D���f���܂�", e);
		}

		// this.endTask = true;

	}

	// �W���u���\�b�hB
	@TaskGroup("groupA")
	@JoinTask(JoinType.NoWait)
	public void doTaskB() {
		for (int i = 1; i < 5 && !this.shutdownTask; i++) {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				log.info("doTaskB��InterruptedException���������܂����D���f���܂�", e);
				break;
			}
			log.info("doTaskB");
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
	public void doTaskC() {
		log.info("doTaskC");
	}

	// ------------------- JOB GROUP B
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@NextTask("jobD")
	public void startGroupB() {
		log.info("startGroupB");
	}

	// �W���u���\�b�hD
	@TaskGroup("groupB")
	public void doTaskD() {
		log.info("doTaskD");
	}

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@NextTask("taskE")
	public void endGroupB() {
		log.info("endGroupB");
	}

	// ------------------- JOB E
	// �W���uE
	@NextTask("taskF")
	public void doTaskE() {
		log.info("doTaskE");
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

	public void setExecuted(boolean executed) {
		this.executed = executed;

	}

	public boolean isExecuted() {
		return executed;
	}

	private boolean startTask = true;

	// ���s������false�ɂ��܂��D
	public void setStartTask(boolean startTask) {
		this.startTask = startTask;
	}

	// true��Ԃ��ƃX�P�W���[������N������܂�
	public boolean getStartTask() {
		return this.startTask;
	}

	private boolean endTask = false;

	// ��~������false�ɂ��܂��D
	public void setEndTask(boolean endTask) {
		this.endTask = endTask;
	}

	// true��Ԃ��ƃX�P�W���[�������~����܂��D
	public boolean getEndTask() {
		return endTask;
	}

	private boolean shutdownTask = false;

	// �V���b�g�_�E��������false�ɂ��܂��D
	public void setShutdownTask(boolean shutdownTask) {
		this.shutdownTask = shutdownTask;
	}

	// true��Ԃ��ƃX�P�W���[������V���b�g�_�E������܂��D
	public boolean getShutdownTask() {
		return shutdownTask;
	}

	public void startScheduler() {
		log.info("startScheduler");
	}

	public void shutdownScheduler() {
		log.info("shutdownScheduler");
	}

}
