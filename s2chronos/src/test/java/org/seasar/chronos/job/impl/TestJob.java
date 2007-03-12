package org.seasar.chronos.job.impl;

import java.util.concurrent.TimeUnit;

import org.seasar.chronos.annotation.job.method.Next;
import org.seasar.framework.log.Logger;

public class TestJob {

	private Logger log = Logger.getLogger(TestJob.class);

	@Next("jobA")
	public void initialize() {
		log.info("initialize");
	}

	public void doJobA() {
		int count = 0;
		try {
			for (int i = 0; i < 10; i++) {
				TimeUnit.SECONDS.sleep(1);
				log.info(count++);
			}
		} catch (InterruptedException e) {
		}
	}

	// �W���u���j�������Ƃ��ɌĂ΂�܂�
	public void destroy() {
		log.info("destroy");
	}
}
