package org.seasar.chronos.core;

public interface Scheduler {

	/**
	 * �X�P�W���[���̐ݒ��Ԃ��܂��D
	 * 
	 * @return �X�P�W���[���̐ݒ�
	 */
	public SchedulerConfiguration getSchedulerConfiguration();

	/**
	 * �X�P�W���[���̐ݒ��ݒ肵�܂��D
	 * 
	 * @param schedulerConfiguration
	 *            �X�P�W���[���̐ݒ�
	 */
	public void setSchedulerConfiguration(
			SchedulerConfiguration schedulerConfiguration);

	/**
	 * �X�P�W���[�����X�^�[�g���܂��D
	 * 
	 */
	public void start();

	/**
	 * �X�P�W���[�����ꎞ��~���܂��D
	 * 
	 */
	public void pause();

	public boolean isPaused();

	/**
	 * �X�P�W���[�����V���b�g�_�E�����܂��D
	 * 
	 */
	public void shutdown();

	/**
	 * �X�P�W���[����ҋ@���܂��D
	 * 
	 */
	public void join();

	public boolean addTask(String taskName);

	public void addTask(Class componentClass);

	public boolean removeTask(Object task);

	public boolean addListener(SchedulerEventListener listener);

	public boolean removeListener(SchedulerEventListener listener);

}