package org.seasar.chronos.smartdeploy.task;

import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.annotation.task.Task;
import org.seasar.chronos.core.annotation.task.method.CloneTask;
import org.seasar.chronos.core.annotation.task.method.JoinTask;
import org.seasar.chronos.core.annotation.task.method.NextTask;
import org.seasar.chronos.core.annotation.type.JoinType;
import org.seasar.framework.log.Logger;

@Task(name = "smart")
public class SmartTask {

	@Override
	public boolean equals(Object obj) {
		boolean result = true;
		SmartTask src = (SmartTask) obj;
		result = result & trigger.equals(src.trigger);
		return result;
	}

	private static final Logger log = Logger.getLogger(SmartTask.class);

	private TaskTrigger trigger;

	public void setNonDelayTrigger(TaskTrigger trigger) {
		this.trigger = trigger;
	}

	public TaskTrigger getTrigger() {
		return this.trigger;
	}

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
