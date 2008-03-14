package org.seasar.chronos.core;

/**
 * スケジューラのインターフェイスです．
 * 
 * @author junichi
 * 
 */
public interface Scheduler {

	/**
	 * スケジューラ設定を返します．
	 * 
	 * @return スケジューラ設定
	 */
	public SchedulerConfiguration getSchedulerConfiguration();

	/**
	 * スケジューラ設定を設定します．
	 * 
	 * @param schedulerConfiguration
	 *            スケジューラ設定
	 */
	public void setSchedulerConfiguration(
			SchedulerConfiguration schedulerConfiguration);

	/**
	 * スケジューラを開始します．
	 */
	public void start();

	public void pause();

	public boolean isPaused();

	public void shutdown();

	public void join();

	public boolean addTask(Class<?> taskComponentClass);

	public boolean removeTask(Class<?> taskComponentClass);

	public boolean addListener(SchedulerEventListener listener);

	public boolean removeListener(SchedulerEventListener listener);

}