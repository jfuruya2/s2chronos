package org.seasar.chronos.extension.store.impl;

import org.seasar.chronos.core.S2TestCaseBase;
import org.seasar.chronos.core.TaskThreadPool;
import org.seasar.chronos.core.threadpool.ThreadPool;
import org.seasar.chronos.extension.store.ThreadPoolStore;

public class ThreadPoolStoreImplTest extends S2TestCaseBase {

	private ThreadPoolStore threadPoolStore;

	public void testLoadFromStoreByObjectId() {
		fail("�܂���������Ă��܂���B");
	}

	public void testLoadFromStoreLong() {
		fail("�܂���������Ă��܂���B");
	}

	public void testLoadFromStoreLongTaskThreadPool() {
		fail("�܂���������Ă��܂���B");
	}

	public void testSaveToStore() {
		TaskThreadPool threadPool = new ThreadPool();
		threadPoolStore.saveToStore(threadPool);
	}

}
