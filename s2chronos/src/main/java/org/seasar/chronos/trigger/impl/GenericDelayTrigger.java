package org.seasar.chronos.trigger.impl;

public class GenericDelayTrigger extends AbstractTrigger {

	private long delay = 0;

	public boolean canEnd() {
		return false;
	}

	public boolean canStart() {
		if (this.isExecuted()) {
			return false;
		}

		boolean startTimeCheck = false;

		// �J�n�����̊m�F
		startTimeCheck = (System.currentTimeMillis() >= delay);

		return startTimeCheck;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = System.currentTimeMillis() + delay;
	}

}
