package org.seasar.s2chronos.job;

import org.seasar.framework.log.Logger;
import org.seasar.s2chronos.annotation.job.Job;
import org.seasar.s2chronos.annotation.job.method.Group;
import org.seasar.s2chronos.annotation.job.method.Next;

@Job
public class ExampleJob {

	private Logger log = Logger.getLogger(ExampleJob.class);

	// �G���g�����\�b�h
	// �ŏ��Ɏ��s����W���u�������̓W���u�O���[�v���w�肵�܂��B
	@Next("groupA")
	public void initialize() {
		log.info("initialize");
	}

	// ------------------- JOB GROUP A
	// ���\�b�h�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	@Next("jobA")
	public void startGroupA() {
		log.info("startGroupA");
	}

	// �W���u���\�b�hA
	@Group("groupA")
	@Next("jobB")
	public void doJobA() {
		log.info("doJobA");
	}

	// �W���u���\�b�hB
	@Group("groupA")
	public void doJobB() {
		log.info("doJobB");
	}

	// ���\�b�h�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	@Next("groupB")
	public void endGroupA() {
		log.info("endGroupA");
	}

	// ------------------- JOB GROUP B
	// ���\�b�h�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
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

	// ���\�b�h�O���[�v���I�������Ƃ��ɌĂ΂�܂�
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

	public boolean canExecute() {
		return true;
	}

}
