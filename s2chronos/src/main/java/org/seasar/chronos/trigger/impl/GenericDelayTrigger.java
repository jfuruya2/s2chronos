package org.seasar.chronos.trigger.impl;

public class GenericDelayTrigger extends AbstractTrigger {

	private long delay = 0;

	public void setDelay(long delay) {
		this.delay = System.currentTimeMillis() + delay;
	}

	public long getDelay() {
		return delay;
	}

	public boolean getStartTask() {
		if (this.isExecuted()) {
			return false;
		}

		boolean startTimeCheck = false;

		// �J�n�����̊m�F
		startTimeCheck = (System.currentTimeMillis() >= delay);

		return startTimeCheck;
	}

	public boolean getEndTask() {
		return false;
	}

	public void setStartTask(boolean startTask) {

	}

	public void setEndTask(boolean endTask) {

	}
}
