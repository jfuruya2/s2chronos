package org.seasar.chronos.core.util;

import java.util.concurrent.atomic.AtomicLong;

public final class ObjectUtil {
	private static final AtomicLong objectIdCounter = new AtomicLong();

	/**
	 * �A�g�~�b�N�ɃI�u�W�F�N�g�p��ID��Ԃ��܂��D
	 * 
	 * @return
	 */
	public static long generateObjectId() {
		return objectIdCounter.addAndGet(1L);
	}

}
