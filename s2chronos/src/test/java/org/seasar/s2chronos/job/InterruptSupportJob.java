package org.seasar.s2chronos.job;

import java.util.concurrent.TimeUnit;

import org.seasar.framework.log.Logger;
import org.seasar.s2chronos.annotation.job.method.Next;

public class InterruptSupportJob {

	private Logger log = Logger.getLogger(InterruptSupportJob.class);

	@Next("jobA")
	public void initialize() {
		log.info("initialize");
	}

	public void doJobA() {
		int count = 0;
		try {
			while (true) {
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