package org.seasar.chronos.delegate;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.tiger.ReflectionUtil;

public class MethodInvoker {

	private static Logger log = Logger.getLogger(MethodInvoker.class);

	private static final String CALLBACK_SUFFIX = "Callback";

	private Class targetClass;

	private Object target;

	private BeanDesc beanDesc;

	private ExecutorService executorService;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param executorService
	 *            ExecutorService
	 * @param target
	 *            �^�[�Q�b�g�I�u�W�F�N�g
	 * @param beanDesc
	 *            BeanDesc
	 */
	public MethodInvoker(ExecutorService executorService, Object target,
			BeanDesc beanDesc) {
		this.executorService = executorService;
		this.beanDesc = beanDesc;
		this.target = target;
		this.targetClass = this.beanDesc.getBeanClass();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param executorService
	 *            ExecutorService
	 * @param componentDef
	 *            �R���|�[�l���g��`
	 */
	public MethodInvoker(ExecutorService executorService,
			ComponentDef componentDef) {
		this.executorService = executorService;
		this.target = componentDef.getComponent();
		this.targetClass = componentDef.getComponentClass();
		this.beanDesc = BeanDescFactory.getBeanDesc(this.targetClass);
	}

	/**
	 * ExecutorService��Ԃ��܂��D
	 * 
	 * @return ExecutorService
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * ���\�b�h�̑��݂̗L����Ԃ��܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @return ����=true, �Ȃ�=false
	 */
	public boolean hasMethod(String methodName) {
		return this.beanDesc.hasMethod(methodName);
	}

	/**
	 * ���\�b�h��Ԃ��܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @return ���\�b�h�I�u�W�F�N�g
	 */
	public Method getMethod(String methodName) {
		return this.beanDesc.getMethod(methodName);
	}

	/**
	 * �w�肵�����\�b�h�𓯊��ŌĂяo���܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @return �߂�l
	 */
	public Object invoke(String methodName) {
		return invoke(methodName, null);
	}

	/**
	 * �w�肵�����\�b�h�𓯊��ŌĂяo���܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @param args
	 *            ���\�b�h����
	 * @return �߂�l
	 */
	public Object invoke(final String methodName, final Object[] args) {
		Object result = (Object) this.beanDesc.invoke(this.target, methodName,
				args);
		return result;
	}

	/**
	 * �w�肵�����\�b�h��񓯊��ŌĂяo���܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @return �񓯊����ʃI�u�W�F�N�g
	 */
	public AsyncResult beginInvoke(final String methodName) {
		return beginInvoke(methodName, null, null, null);
	}

	/**
	 * �w�肵�����\�b�h��񓯊��ŌĂяo���܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @param args
	 *            ���\�b�h�̈���
	 * @return �񓯊����ʃI�u�W�F�N�g
	 */
	public AsyncResult beginInvoke(final String methodName, final Object[] args) {
		return beginInvoke(methodName, args, null, null);
	}

	/**
	 * �w�肵�����\�b�h��񓯊��ŌĂяo���܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @param methodCallback
	 *            ���\�b�h�R�[���o�b�N(���\�b�h���ɂ͔�Public�����w��ł��܂�)
	 * @param state
	 *            �X�e�[�g
	 * @return �񓯊����ʃI�u�W�F�N�g
	 */
	public AsyncResult beginInvoke(final String methodName,
			final MethodCallback methodCallback, final Object state) {
		return beginInvoke(methodName, null, methodCallback, state);
	}

	/**
	 * �w�肵�����\�b�h��񓯊��ŌĂяo���܂��D
	 * 
	 * @param methodName
	 *            ���\�b�h��
	 * @param args
	 *            ���\�b�h�̈�����
	 * @param methodCallback
	 *            ���\�b�h�R�[���o�b�N�I�u�W�F�N�g
	 * @param state
	 *            �X�e�[�g
	 * @return �񓯊����ʃI�u�W�F�N�g
	 */
	public AsyncResult beginInvoke(final String methodName,
			final Object[] args, final MethodCallback methodCallback,
			final Object state) {

		final AsyncResult asyncResult = new AsyncResult();

		Future<Object> future = this.executorService
				.submit(new Callable<Object>() {
					public Object call() throws Exception {
						synchronized (asyncResult) {
							asyncResult.notify();
						}
						// �Ώۃ��\�b�h�����s
						Object result = invoke(methodName, args);

						if (methodCallback != null) {
							ExecutorService es = Executors
									.newSingleThreadExecutor();
							// ����ɃR�[���o�b�N���X���b�h�v�[��������s
							es.submit(new Callable<Void>() {
								public Void call() throws Exception {
									callbackHandler(methodName, methodCallback,
											asyncResult);
									return null;
								}
							});
						}

						return result;
					}

					// �R�[���o�b�N�����s���܂�
					private void callbackHandler(final String methodName,
							final MethodCallback methodCallback,
							final AsyncResult asyncResult) throws Exception {
						try {
							StringBuffer callbackMethodName = new StringBuffer(
									methodCallback.getMethodName());
							if (callbackMethodName == null) {
								callbackMethodName.append(methodName);
								callbackMethodName.append(CALLBACK_SUFFIX);
							}
							Method mt = ReflectionUtil.getDeclaredMethod(
									methodCallback.getTargetClass(),
									callbackMethodName.toString(),
									AsyncResult.class);
							mt.setAccessible(true);
							ReflectionUtil.invoke(mt, methodCallback
									.getTarget(), asyncResult);
						} catch (Exception ex) {
							log.error(ex);
							throw ex;
						}

					}
				});

		synchronized (asyncResult) {
			asyncResult.setFuture(future);
			asyncResult.setState(state);
			try {
				asyncResult.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return asyncResult;
	}

	/**
	 * �񓯊��Ăяo���̖߂�l��Ԃ��܂��D<br>
	 * �񓯊��Ăяo�����������Ă��Ȃ��ꍇ�́C������ҋ@���܂��D
	 * 
	 * @param asyncResult
	 *            �񓯊����ʃI�u�W�F�N�g
	 * @return �߂�l�̃I�u�W�F�N�g
	 * @throws InterruptedException
	 *             ��O
	 */
	public Object endInvoke(AsyncResult asyncResult)
			throws InterruptedException {
		try {
			return asyncResult.getFuture().get();
		} catch (ExecutionException e) {
			throw new ExecutionRuntimeException(e);
		} catch (InterruptedException e) {
			log.warn(e);
			throw e;
		}
	}

	/**
	 * �񓯊��Ăяo�����L�����Z�����܂��D
	 * 
	 * @param asyncResult
	 *            �񓯊����ʃI�u�W�F�N�g
	 * @return �߂�l�̃I�u�W�F�N�g
	 */
	public boolean cancelInvoke(AsyncResult asyncResult) {
		return cancelInvoke(asyncResult, true);
	}

	/**
	 * �񓯊��Ăяo�����L�����Z�����܂��D
	 * 
	 * @param asyncResult
	 * @param shutdown
	 * @return
	 */
	public boolean cancelInvoke(AsyncResult asyncResult, boolean shutdown) {
		return asyncResult.getFuture().cancel(shutdown);
	}

	public void cancelInvokes() {
		this.cancelInvokes(true);
	}

	public void cancelInvokes(boolean shutdown) {
		if (shutdown) {
			this.executorService.shutdownNow();
		} else {
			this.executorService.shutdown();
		}
	}

	public void awaitInvokes(long time, TimeUnit unit)
			throws InterruptedException {
		try {
			this.executorService.awaitTermination(time, unit);
		} catch (InterruptedException e) {
			log.warn(e);
			throw e;
		}
	}

}