package org.seasar.s2chronos.job.impl;

import java.util.concurrent.TimeUnit;

import org.seasar.framework.log.Logger;
import org.seasar.s2chronos.ThreadPoolType;
import org.seasar.s2chronos.annotation.job.Job;
import org.seasar.s2chronos.annotation.job.method.Group;
import org.seasar.s2chronos.annotation.job.method.Join;
import org.seasar.s2chronos.annotation.job.method.Next;
import org.seasar.s2chronos.annotation.type.JoinType;

@Job
public class ExampleJobImpl {

	private Logger log = Logger.getLogger(ExampleJobImpl.class);

	// �W���u���\�b�h���ǂ̃^�C�v�̃X���b�h�v�[���Ŏ��s���邩�Ԃ��܂�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#getThreadPoolType()
	 */
	public ThreadPoolType getThreadPoolType() {
		return ThreadPoolType.CACHED;
	}

	// FIXED�̏ꍇ�́A�v�[���T�C�Y��Ԃ��܂��B
	// ex)2�Ƃ����ꍇ������2�X���b�h�܂ł̑��d�x���w��\�B
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#getThreadPoolSize()
	 */
	public int getThreadPoolSize() {
		return 2;
	}

	// �G���g�����\�b�h
	// �ŏ��Ɏ��s����W���u�������̓W���u�O���[�v���w�肵�܂��B
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#initialize()
	 */
	@Next("groupA")
	public void initialize() {
		log.info("initialize");
	}

	// ------------------- JOB GROUP A
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#startGroupA()
	 */
	@Next("jobA")
	public void startGroupA() {
		log.info("startGroupA");
	}

	// �W���u���\�b�hA
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#doJobA()
	 */
	@Group("groupA")
	@Next("jobB")
	@Join(JoinType.NoWait)
	public void doJobA() throws Exception {
		for (int i = 1; i < 5; i++) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				break;
			}
			log.info("doJobA");
		}
	}

	// �W���u���\�b�hB
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#doJobB()
	 */
	@Group("groupA")
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
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#endGroupA()
	 */
	@Next("groupB")
	public void endGroupA() {
		log.info("endGroupA");
	}

	// ------------------- JOB GROUP B
	// �W���u�O���[�v���J�n�����Ƃ��ɌĂ΂�܂�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#startGroupB()
	 */
	@Next("jobC")
	public void startGroupB() {
		log.info("startGroupB");
	}

	// �W���u���\�b�hC
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#doJobC()
	 */
	@Group("groupB")
	@Next("jobD")
	public void doJobC() {
		log.info("doJobC");
	}

	// �W���u���\�b�hD
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#doJobD()
	 */
	@Group("groupB")
	public void doJobD() {
		log.info("doJobD");
	}

	// �W���u�O���[�v���I�������Ƃ��ɌĂ΂�܂�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#endGroupB()
	 */
	@Next("jobE")
	public void endGroupB() {
		log.info("endGroupB");
	}

	// ------------------- JOB E
	// �W���uE
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#doJobE()
	 */
	@Next("jobF")
	public void doJobE() {
		log.info("doJobE");
	}

	// ------------------- JOB F
	// �W���uF
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#doJobF()
	 */
	public void doJobF() {
		log.info("doJobF");
	}

	// �W���u���j�������Ƃ��ɌĂ΂�܂�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#destroy()
	 */
	public void destroy() {
		log.info("destroy");
	}

	// �W���u�ŗ�O���X���[�����ƌĂ΂�܂�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#cancel()
	 */
	public void cancel() {
		log.info("cancel");
	}

	// ���s�\����Ԃ�
	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#canExecute()
	 */
	public boolean canExecute() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#startScheduler()
	 */
	public void startScheduler() {
		log.info("startScheduler");
	}

	/* (non-Javadoc)
	 * @see org.seasar.s2chronos.job.impl.ExampleJob#shutdownScheduler()
	 */
	public void shutdownScheduler() {
		log.info("shutdownScheduler");
	}

}
