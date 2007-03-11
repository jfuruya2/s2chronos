package org.seasar.chronos.trigger;

public interface Trigger {

	/**
	 * �g���K�[���I����ԂȂ�true�A����ȊO�Ȃ�false��Ԃ��܂��B
	 * 
	 * @return
	 */
	public boolean canEnd();

	/**
	 * �g���K�[���J�n��ԂȂ�true�A����ȊO�Ȃ�false��Ԃ��܂��B
	 * 
	 * @return
	 */
	public boolean canStart();

	public String getDescription();

	public long getId();

	public Object getJob();

	// public JobAdaptor getJobAdaptor();

	public String getName();

	public boolean isExecuted();

	public void setDescription(String description);

	public void setExecuted(boolean executed);

	public void setId(long triggerId);

	public void setJob(Object jobComponent);

	public void setName(String name);

}