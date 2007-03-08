package org.seasar.s2chronos.job;

import org.seasar.s2chronos.ThreadPoolType;
import org.seasar.s2chronos.annotation.job.method.Group;
import org.seasar.s2chronos.annotation.job.method.Join;
import org.seasar.s2chronos.annotation.job.method.Next;
import org.seasar.s2chronos.annotation.type.JoinType;

public interface ExampleJob {

	// �W���u���\�b�h���ǂ̃^�C�v�̃X���b�h�v�[���Ŏ��s���邩�Ԃ��܂�
	public abstract ThreadPoolType getThreadPoolType();

	// FIXED�̏ꍇ�́A�v�[���T�C�Y��Ԃ��܂��B
	// ex)2�Ƃ����ꍇ������2�X���b�h�܂ł̑��d�x���w��\�B
	public abstract int getThreadPoolSize();

	// �G���g�����\�b�h
	// �ŏ��Ɏ��s����W���u�������̓W���u�O���[�v���w�肵�܂��B
	@Next("groupA")
	public abstract void initialize();

	// ------------------- JOB GROUP A
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@Next("jobA")
	public abstract void startGroupA();

	// �W���u���\�b�hA
	@Group("groupA")
	@Next("jobB")
	@Join(JoinType.NoWait)
	public abstract void doJobA() throws Exception;

	// �W���u���\�b�hB
	@Group("groupA")
	@Join(JoinType.NoWait)
	public abstract void doJobB();

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@Next("groupB")
	public abstract void endGroupA();

	// ------------------- JOB GROUP B
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@Next("jobC")
	public abstract void startGroupB();

	// �W���u���\�b�hC
	@Group("groupB")
	@Next("jobD")
	public abstract void doJobC();

	// �W���u���\�b�hD
	@Group("groupB")
	public abstract void doJobD();

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@Next("jobE")
	public abstract void endGroupB();

	// ------------------- JOB E
	// �W���uE
	@Next("jobF")
	public abstract void doJobE();

	// ------------------- JOB F
	// �W���uF
	public abstract void doJobF();

	// �W���u���j�������Ƃ��ɌĂ΂�܂�
	public abstract void destroy();

	// �W���u�ŗ�O���X���[�����ƌĂ΂�܂�
	public abstract void cancel();

	// ���s�\����Ԃ�
	public abstract boolean canExecute();

	public abstract void startScheduler();

	public abstract void shutdownScheduler();

}