package org.seasar.chronos.core.trigger;

import java.util.Date;

public class TimedTrigger extends AbstractTrigger {

	private static final long serialVersionUID = -6653656597493889393L;

	private Date startTime;

	private Date endTime;

	public TimedTrigger() {

	}

	public TimedTrigger(String name) {
		super(name);
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

	public Boolean getStartTask() {

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

	public Boolean getEndTask() {

		boolean endTimeCheck = false;

		// �I�������̊m�F
		if (endTime != null) {
			endTimeCheck = (System.currentTimeMillis() >= endTime.getTime());
		}

		return endTimeCheck;
	}

	public void setStartTask(Boolean startTask) {

	}

	public void setEndTask(Boolean endTask) {

	}

}
