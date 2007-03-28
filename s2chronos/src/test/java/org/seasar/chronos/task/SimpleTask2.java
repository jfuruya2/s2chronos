package org.seasar.chronos.task;

import org.seasar.chronos.annotation.task.Task;
import org.seasar.chronos.annotation.task.method.CloneTask;
import org.seasar.chronos.annotation.task.method.JoinTask;
import org.seasar.chronos.annotation.task.method.NextTask;
import org.seasar.chronos.annotation.type.JoinType;
import org.seasar.framework.log.Logger;

@Task(name = "simple2")
public class SimpleTask2 {

	private static Logger log = Logger.getLogger(SimpleTask2.class);

	private boolean startTask = true;

	// true��Ԃ��ƃ^�X�N�̋N�����J�n�����
	public synchronized boolean getStartTask() {
		return this.startTask;
	}

	// �^�X�N�����s�����Ƃ��ɍŏ��ɌĂ΂��
	@NextTask("taskA")
	public synchronized void initialize() {
		log.info("SimpleTask2::initialize");
	}

	// �^�X�N���\�b�hA �{��
	// �J�ڐ��ÓI�ɐݒ肵�C�񓯊��Ŏ��s
	@NextTask("taskB")
	@JoinTask(JoinType.NoWait)
	public synchronized void doTaskA() {
		log.info("SimpleTask2::doTaskA");
	}

	// �^�X�N���\�b�hB �{��
	// �����Ŏ��s���J�ڐ�𓮓I�Ɏw�肷��
	@JoinTask(JoinType.Wait)
	public synchronized String doTaskB() {
		log.info("SimpleTask2::doTaskB");
		return "taskC";
	}

	// �^�X�N���\�b�hC �{��
	// �񓯊���100�^�X�N���\�b�h�𐶐����Ď��s
	@JoinTask(JoinType.NoWait)
	@CloneTask(20)
	public synchronized void doTaskC() {
		log.info(">>SimpleTask2::doTaskC");
	}

	// ���ׂẴ^�X�N���I��������Ă΂��
	// @NextTask("example")
	public synchronized void destroy() {
		log.info("SimpleTask2::destroy");
	}

}
