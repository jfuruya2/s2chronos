package org.seasar.chronos.delegate;

import java.util.concurrent.Executors;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.ComponentDef;
import org.seasar.s2chronos.job.ExampleJob;

public class MethodInvokerTest extends S2TestCase {

	private static final String PATH = "app.dicon";

	private MethodInvoker target;

	protected void setUp() throws Exception {
		this.include(PATH);
		ComponentDef componentDef = this.getComponentDef(ExampleJob.class);
		target = new MethodInvoker(Executors.newSingleThreadExecutor(),
				componentDef);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testHasMethod() {
		boolean ret = target.hasMethod("initialize");
		assertEquals(ret, true);
	}

	public void testGetMethod() {
		fail("�܂���������Ă��܂���B");
	}

	public void testInvokeString() {
		fail("�܂���������Ă��܂���B");
	}

	public void testInvokeStringObjectArray() {
		fail("�܂���������Ă��܂���B");
	}

	public void testBeginInvokeString() {
		fail("�܂���������Ă��܂���B");
	}

	public void testBeginInvokeStringMethodCallbackObject() {
		fail("�܂���������Ă��܂���B");
	}

	public void testBeginInvokeStringObjectArrayMethodCallbackObject() {
		fail("�܂���������Ă��܂���B");
	}

	public void testEndInvoke() {
		fail("�܂���������Ă��܂���B");
	}

	public void testCancelInvokeAsyncResult() {
		fail("�܂���������Ă��܂���B");
	}

	public void testCancelInvokeAsyncResultBoolean() {
		fail("�܂���������Ă��܂���B");
	}

}
