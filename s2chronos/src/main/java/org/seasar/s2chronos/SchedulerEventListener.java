package org.seasar.s2chronos;

public interface SchedulerEventListener {

	public void startScheduler(Scheduler scheduler);

	public void shutdownScheduler(Scheduler scheduler);

	public void startJobGroup(Scheduler scheduler, Object job);

	public void endJobGroup(Scheduler scheduler, Object job);

	public void startJob(Scheduler scheduler, Object job);

	public void endJob(Scheduler scheduler, Object job);

	public void cancelJob(Scheduler scheduler, Object job);

	public void cancelJobGroup(Scheduler scheduler, Object jobGroup);

}
