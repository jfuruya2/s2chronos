package org.seasar.chronos.test.task;

import org.seasar.chronos.TaskTrigger;
import org.seasar.chronos.annotation.task.Task;
import org.seasar.chronos.annotation.task.method.CloneTask;
import org.seasar.chronos.annotation.task.method.JoinTask;
import org.seasar.chronos.annotation.task.method.NextTask;
import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.framework.log.Logger;

@Task(name = "smart")
public class SmartTask {

	private static Logger log = Logger.getLogger(SmartTask.class);

	private TaskTrigger trigger;

	public void setNonDelayTrigger(TaskTrigger trigger) {
		this.trigger = trigger;
	}

	public TaskTrigger getTrigger() {
		return this.trigger;
	}

	// private boolean startTask = true;
	//
	// // true��Ԃ��ƃ^�X�N�̋N�����J�n�����
	// public synchronized boolean getStartTask() {
	// return this.startTask;
	// }

	// �^�X�N�����s�����Ƃ��ɍŏ��ɌĂ΂��
	@NextTask("taskA")
	public synchronized void initialize() {
		log.info("SmartTask::initialize");
	}

	// �^�X�N���\�b�hA �{��
	// �J�ڐ��ÓI�ɐݒ肵�C�񓯊��Ŏ��s
	@NextTask("taskB")
	@JoinTask(JoinType.NoWait)
	public synchronized void doTaskA() {
		log.info("SmartTask::doTaskA");
	}

	// �^�X�N���\�b�hB �{��
	// �����Ŏ��s���J�ڐ�𓮓I�Ɏw�肷��
	@JoinTask(JoinType.Wait)
	public synchronized String doTaskB() {
		log.info("SmartTask::doTaskB");
		return "taskC";
	}

	// �^�X�N���\�b�hC �{��
	// �񓯊���100�^�X�N���\�b�h�𐶐����Ď��s
	@JoinTask(JoinType.NoWait)
	@CloneTask(10)
	public synchronized void doTaskC() {
		log.info("<<SmartTask::doTaskC");
	}

	// ���ׂẴ^�X�N���I��������Ă΂��
	@NextTask("example")
	public synchronized void destroy() {
		log.info("SmartTask::destroy");
	}

}
