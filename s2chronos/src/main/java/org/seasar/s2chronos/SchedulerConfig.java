package org.seasar.s2chronos;

/**
 * �X�P�W���[���̐ݒ���Ǘ����܂�.
 * 
 * @author junichi
 * 
 */
public final class SchedulerConfig {

	private boolean autoShutdownWithFinshedAllJob;

	public boolean isAutoShutdownWithFinshedAllJob() {
		return autoShutdownWithFinshedAllJob;
	}

	public void setAutoShutdownWithFinshedAllJob(
			boolean autoShutdownWithFinshedAllJob) {
		this.autoShutdownWithFinshedAllJob = autoShutdownWithFinshedAllJob;
	}

}
