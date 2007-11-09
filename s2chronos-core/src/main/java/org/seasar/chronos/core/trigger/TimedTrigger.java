package org.seasar.chronos.core.trigger;

import java.util.Date;

public class TimedTrigger extends AbstractTrigger {

	private static final long serialVersionUID = -6653656597493889393L;

	private Date startTime;

	private Date endTime;

	public TimedTrigger() {
		super("timedTrigger");
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

	public boolean isStartTask() {

		if (this.isExecute()) {
			return false;
		}

		boolean startTimeCheck = false;

		// 開始時刻の確認
		if (startTime != null) {
			startTimeCheck = (System.currentTimeMillis() >= startTime.getTime());
		}

		return startTimeCheck;
	}

	public boolean isEndTask() {

		boolean endTimeCheck = false;

		// 終了時刻の確認
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
