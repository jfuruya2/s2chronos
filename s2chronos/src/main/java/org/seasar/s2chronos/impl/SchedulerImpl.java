package org.seasar.s2chronos.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.seasar.s2chronos.Scheduler;
import org.seasar.s2chronos.SchedulerConfig;
import org.seasar.s2chronos.SchedulerEventListener;
import org.seasar.s2chronos.exception.SchedulerException;
import org.seasar.s2chronos.trigger.Trigger;

public class SchedulerImpl implements Scheduler {

	private ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	public void addListener(SchedulerEventListener listener) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public boolean addTriggerr(Trigger trigger) throws SchedulerException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

	public SchedulerConfig getConfig() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	public void join() throws SchedulerException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public void pause() throws SchedulerException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public void removeListener(SchedulerEventListener listener) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public boolean removeTrigger(Trigger trigger) throws SchedulerException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return false;
	}

	public void setConfig(SchedulerConfig config) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public void shutdown() throws SchedulerException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public void shutdown(boolean waitAllJobFinish) throws SchedulerException {
		executorService.shutdown();
		try {
			executorService.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new SchedulerException(e);
		}
	}

	public void start() throws SchedulerException {

		Future<Void> future = executorService.submit(new Callable<Void>() {

			public Void call() throws Exception {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				return null;
			}

		});
	}

}
