package org.seasar.chronos.core.trigger;

public class CDelayTrigger extends AbstractTrigger {

	private static final long serialVersionUID = 1L;

	private long delay = 0;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public CDelayTrigger() {
		super("delayTrigger");
	}

	public CDelayTrigger(long delay) {
		this();
		this.delay = delay;
	}

	@Override
	public boolean isReSchedule() {
		return false;
	}

	public void setDelay(long delay) {
		this.delay = System.currentTimeMillis() + delay;
	}

	public long getDelay() {
		return delay;
	}

	public boolean isStartTask() {
		if (this.isExecute()) {
			return false;
		}

		boolean startTimeCheck = false;

		// 開始時刻の確認
		startTimeCheck = (System.currentTimeMillis() >= delay);

		return startTimeCheck;
	}

	public boolean isEndTask() {
		return false;
	}

	public void setStartTask(boolean startTask) {

	}

	public void setEndTask(boolean endTask) {

	}

}
