package org.seasar.chronos.trigger.impl;

import java.util.Date;

public class GenericTrigger extends AbstractTrigger {

	private Date startTime;

	private Date endTime;

	public GenericTrigger() {

	}

	public GenericTrigger(String name) {
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

	public boolean canEnd() {

		boolean endTimeCheck = false;

		// �I�������̊m�F
		if (endTime != null) {
			endTimeCheck = (System.currentTimeMillis() >= endTime.getTime());
		}

		return endTimeCheck;
	}

	public boolean canStart() {

		if (this.isExecuted()) {
			return false;
		}

		boolean startTimeCheck = false;

		// �J�n�����̊m�F
		if (startTime != null) {
			startTimeCheck = (System.currentTimeMillis() >= startTime.getTime());
		}

		return startTimeCheck;
	}

}
