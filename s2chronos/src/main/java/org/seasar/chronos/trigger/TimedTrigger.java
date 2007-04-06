package org.seasar.chronos.trigger;

import java.util.Date;

import org.seasar.chronos.store.trigger.TimedTriggerStore;

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

	@Override
	public void save() {
		this.store.saveToStore(this);
	}

	@Override
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

		if (this.isExecute()) {
			return false;
		}

		boolean startTimeCheck = false;

		// �J�n�����̊m�F
		if (startTime != null) {
			startTimeCheck = (System.currentTimeMillis() >= startTime.getTime());
		}

		return startTimeCheck;
	}

	public boolean getEndTask() {

		boolean endTimeCheck = false;

		// �I�������̊m�F
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
