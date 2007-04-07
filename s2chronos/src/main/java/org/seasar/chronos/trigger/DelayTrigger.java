package org.seasar.chronos.trigger;

public class DelayTrigger extends AbstractTrigger {

	private static final long serialVersionUID = -3996902051005552696L;

	private long delay = 0;

	public void setDelay(long delay) {
		this.delay = System.currentTimeMillis() + delay;
	}

	public long getDelay() {
		return delay;
	}

	public boolean getStartTask() {
		if (this.isExecute()) {
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
