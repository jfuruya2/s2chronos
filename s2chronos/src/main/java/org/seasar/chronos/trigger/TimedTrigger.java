package org.seasar.chronos.trigger;

import java.util.Date;

import org.seasar.chronos.store.TimedTriggerStore;

public class TimedTrigger extends AbstractTrigger {

	private static final long serialVersionUID = -6653656597493889393L;

	private Date startTime;

	private Date endTime;

	private TimedTriggerStore store;

	public void setTimedTriggerStore(TimedTriggerStore store) {
		this.store = store;
	}

	public TimedTrigger() {

	}

	public TimedTrigger(String name) {
		super(name);
	}

	public void save() {
		this.store.saveToStore(this);
	}

	public void load() {
		this.store.loadFromStore(this.getId(), this);
	}

	public void setStartTime(Date startDate) {
		this.startTime = startDate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setEndTime(Date endDate) {
		this.endTime = endDate;
	}

	public Date getEndTime() {
		return endTime;
	}

	public boolean getStartTask() {

		if (this.isExecuted()) {
			return false;
		}

		boolean startTimeCheck = false;

		// 開始時刻の確認
		if (startTime != null) {
			startTimeCheck = (System.currentTimeMillis() >= startTime.getTime());
		}

		return startTimeCheck;
	}

	public boolean getEndTask() {

		boolean endTimeCheck = false;

		// 終了時刻の確認
		if (endTime != null) {
			endTimeCheck = (System.currentTimeMillis() >= endTime.getTime());
		}

		return endTimeCheck;
	}

	public void setStartTask(boolean startTask) {

	}

	public void setEndTask(boolean endTask) {

	}

}
