package org.seasar.chronos.task;

import org.seasar.chronos.annotation.task.Task;
import org.seasar.chronos.annotation.task.method.CloneTask;
import org.seasar.chronos.annotation.task.method.JoinTask;
import org.seasar.chronos.annotation.task.method.NextTask;
import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.framework.log.Logger;

@Task(name = "simple")
public class SimpleTask {

	private static Logger log = Logger.getLogger(SimpleTask.class);

	private boolean startTask = true;

	// true��Ԃ��ƃ^�X�N�̋N�����J�n�����
	public boolean getStartTask() {
		return this.startTask;
	}

	// �^�X�N�����s�����Ƃ��ɍŏ��ɌĂ΂��
	@NextTask("taskA")
	public void initialize() {
		log.info("initialize");
	}

	// �^�X�N���\�b�hA �{��
	// �J�ڐ��ÓI�ɐݒ肵�C�񓯊��Ŏ��s
	@NextTask("taskB")
	@JoinTask(JoinType.NoWait)
	public void doTaskA() {
		log.info("doTaskA");
	}

	// �^�X�N���\�b�hB �{��
	// �����Ŏ��s���J�ڐ�𓮓I�Ɏw�肷��
	@JoinTask(JoinType.Wait)
	public String doTaskB() {
		log.info("doTaskB");
		if (System.currentTimeMillis() % 2 == 0) {
			return "taskA";
		}
		return "taskC";
	}

	// �^�X�N���\�b�hC �{��
	// �񓯊���100�^�X�N���\�b�h�𐶐����Ď��s
	@JoinTask(JoinType.NoWait)
	@CloneTask(3)
	public void doTaskC() {
		log.info("doTaskC");
	}

	// ���ׂẴ^�X�N���I��������Ă΂��
	// @NextTask("example")
	public void destroy() {
		log.info("destroy");
	}

}
