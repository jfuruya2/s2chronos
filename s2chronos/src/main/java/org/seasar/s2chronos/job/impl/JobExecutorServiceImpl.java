package org.seasar.s2chronos.job.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.log.Logger;
import org.seasar.s2chronos.ThreadPoolType;
import org.seasar.s2chronos.annotation.job.method.Clone;
import org.seasar.s2chronos.annotation.job.method.Group;
import org.seasar.s2chronos.annotation.job.method.Join;
import org.seasar.s2chronos.annotation.job.method.Next;
import org.seasar.s2chronos.annotation.type.JoinType;
import org.seasar.s2chronos.exception.InvalidNextJobMethodException;
import org.seasar.s2chronos.job.JobExecutorService;

public class JobExecutorServiceImpl implements JobExecutorService {

	private Logger log = Logger.getLogger(JobExecutorServiceImpl.class);

	private static final String METHOD_PREFIX_NAME_DO = "do";

	private static final String METHOD_PREFIX_NAME_END = "end";

	private static final String METHOD_PREFIX_NAME_START = "start";

	private static final boolean WAIT_DEFAULT = true;

	private boolean jobInitialized = false;

	private Object job;

	private List<Method> allJobMethodList = new ArrayList<Method>();

	private HashMap<String, HashMap<String, Method>> allJobMethodMap = new HashMap<String, HashMap<String, Method>>();

	private BeanDesc beanDesc;

	private ExecutorService executorService = null;

	public JobExecutorServiceImpl() {

	}

	public void setJobComponentDef(ComponentDef jobComoponetDef) {
		this.job = jobComoponetDef.getComponent();
		this.beanDesc = BeanDescFactory.getBeanDesc(jobComoponetDef
				.getComponentClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.seasar.s2chronos.job.JobExecutorService#initialize()
	 */
	public String initialize() {

		this.preparedJob();

		String result = null;

		if (this.beanDesc.hasMethod("initialize")) {
			ResultSet resultSet = this.invokeMethod("initialize");
			result = resultSet.getNext();
		}

		jobInitialized = true;
		return result;
	}

	private void preparedJob() {

		PropertyDesc threadPoolType = this.beanDesc
				.getPropertyDesc("threadPoolType");
		ThreadPoolType type = (ThreadPoolType) threadPoolType.getValue(job);

		if (type == ThreadPoolType.FIXED) {
			int threadSize = getThreadPoolSize();
			executorService = Executors.newFixedThreadPool(threadSize);
		} else if (type == ThreadPoolType.CACHED) {
			executorService = Executors.newCachedThreadPool();
		} else if (type == ThreadPoolType.SINGLE) {
			executorService = Executors.newSingleThreadExecutor();
		} else if (type == ThreadPoolType.SCHEDULED) {
			int threadSize = getThreadPoolSize();
			executorService = Executors.newScheduledThreadPool(threadSize);
		}

		Class clazz = this.beanDesc.getBeanClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith(METHOD_PREFIX_NAME_DO)) {
				allJobMethodList.add(method);
				Group group = method.getAnnotation(Group.class);
				String groupName;
				if (group != null) {
					groupName = group.value();
				} else {
					groupName = clazz.getName();
				}
				HashMap<String, Method> jobMethodMap = allJobMethodMap
						.get(groupName);
				if (null == jobMethodMap) {
					jobMethodMap = new HashMap<String, Method>();
					allJobMethodMap.put(groupName, jobMethodMap);
				}
				jobMethodMap.put(methodName, method);

			}
		}

	}

	private int getThreadPoolSize() {
		PropertyDesc threadPoolSize = this.beanDesc
				.getPropertyDesc("threadPoolSize");
		int threadSize = (Integer) threadPoolSize.getValue(job);
		return threadSize;
	}

	private class ResultSet {

		private List<Future<Object>> futureList;

		private boolean wait;

		private String next;

		public ResultSet(List<Future<Object>> futureList, boolean wait,
				String next) {
			this.futureList = futureList;
			this.wait = wait;
			this.next = next;
		}

		public Future<Object> getFuture() {
			return futureList.get(0);
		}

		public List<Future<Object>> getFutureList() {
			return futureList;
		}

		public void setNext(String next) {
			this.next = next;
		}

		public String getNext() {
			return next;
		}

		public boolean isWait() {
			return wait;
		}
	}

	private ResultSet invokeMethod(final String function) {
		String result = null;
		long cloneSize = 1;
		boolean wait = WAIT_DEFAULT;
		Method method = this.beanDesc.getMethod(function);
		Join join = method.getAnnotation(Join.class);
		if (join != null) {
			wait = (join.value() == JoinType.Wait);
		}
		Next next = method.getAnnotation(Next.class);
		if (next != null) {
			result = next.value();
		}
		Clone clone = method.getAnnotation(Clone.class);
		if (clone != null) {
			cloneSize = clone.value();
		}

		List<Future<Object>> futureList = new ArrayList<Future<Object>>();
		for (long i = 0; i < cloneSize; i++) {
			// スレッドプールでジョブを呼び出す
			Future<Object> future = executorService
					.submit(new Callable<Object>() {
						public Object call() throws Exception {
							Object result = beanDesc
									.invoke(job, function, null);
							return result;
						}
					});
			futureList.add(future);
		}
		return new ResultSet(futureList, wait, result);

	}

	private ResultSet invokeJobMethod(String jobName) {

		String firstChar = jobName.substring(0, 1);
		String afterString = jobName.substring(1);
		final String function = METHOD_PREFIX_NAME_DO + firstChar.toUpperCase()
				+ afterString;

		return invokeMethod(function);
	}

	private ResultSet invokeStartJobGroupMethod(String startGroupName) {
		ResultSet resultSet = invokeMethod(METHOD_PREFIX_NAME_START
				+ startGroupName);
		return resultSet;
	}

	private ResultSet invokeEndJobGroupMethod(String endGroupName) {
		ResultSet resultSet = invokeMethod(METHOD_PREFIX_NAME_END
				+ endGroupName);
		return resultSet;
	}

	private void callJobGroupMethods(String startGroupName)
			throws InterruptedException, InvalidNextJobMethodException,
			ExecutionException {

		String firstChar = startGroupName.substring(0, 1);
		String afterString = startGroupName.substring(1);

		String groupName = firstChar.toUpperCase() + afterString;

		ResultSet resultSet = invokeStartJobGroupMethod(groupName);

		if (resultSet.getNext() == null) {
			log.debug("ジョブグループが開始しませんでした");
			return;
		} else if (isGroupMethod(resultSet.getNext())) {
			log.debug("startGroupの次はメソッドを指定してください");
			throw new InvalidNextJobMethodException(
					"startGroupの次はメソッドを指定してください");
		}

		callJobMethods(resultSet.getNext());

		resultSet = invokeEndJobGroupMethod(groupName);

		if (resultSet.getNext() == null) {
			return;
		} else if (isGroupMethod(resultSet.getNext())) {
			callJobGroupMethods(resultSet.getNext());
		} else {
			callJobMethods(resultSet.getNext());
		}
	}

	private void callJobMethods(String startJobName)
			throws InterruptedException, InvalidNextJobMethodException,
			ExecutionException {

		List<Future<Object>> futureList = new ArrayList<Future<Object>>();
		// ジョブを呼び出す
		ResultSet resultSet = null;
		String nextMethodName = startJobName;
		while (true) {
			resultSet = invokeJobMethod(nextMethodName);
			futureList.addAll(resultSet.getFutureList());
			if (resultSet.isWait()) {
				Object returnObject = null;
				for (Future<Object> f : resultSet.getFutureList()) {
					try {
						returnObject = f.get();
					} catch (ExecutionException e) {
						log.warn("ジョブから例外がスローされました", e);
						f.cancel(true);
						throw e;
					}
				}
				// 同期の場合で戻り値にStringでジョブ名を返した場合は遷移先を上書き
				if (returnObject instanceof String) {
					resultSet.setNext((String) returnObject);
				}
			}
			nextMethodName = resultSet.getNext();
			if (nextMethodName == null) {
				break;
			} else if (isGroupMethod(nextMethodName)) {
				callJobGroupMethods(nextMethodName);
				break;
			}
		}

		for (Future<Object> f : futureList) {
			try {
				f.get();
			} catch (ExecutionException e) {
				log.warn("ジョブから例外がスローされました", e);
				f.cancel(true);
				throw e;
			}
		}

	}

	private boolean isGroupMethod(String groupName) {
		HashMap<String, Method> jobMethod = allJobMethodMap.get(groupName);
		if (jobMethod != null) {
			return true;
		}
		return false;
	}

	public void callJob(String startJobName) throws InterruptedException,
			InvalidNextJobMethodException, ExecutionException {

		if (isGroupMethod(startJobName)) {
			callJobGroupMethods(startJobName);
		} else {
			callJobMethods(startJobName);
		}

	}

	public void cancel() {
		this.executorService.shutdownNow();
		if (this.beanDesc.hasMethod("cancel")) {
			this.beanDesc.invoke(this.job, "cancel", null);
		}
	}

	public boolean await(long time, TimeUnit timeUnit)
			throws InterruptedException {
		return this.executorService.awaitTermination(time, timeUnit);
	}

	public void destroy() {
		if (!this.jobInitialized || this.executorService.isShutdown()) {
			return;
		}

		if (this.beanDesc.hasMethod("destroy")) {
			this.invokeMethod("destroy");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.seasar.s2chronos.job.JobExecutorService#canExecute()
	 */
	public boolean canExecute() {
		if (this.beanDesc.hasMethod("canExecute")) {
			return (Boolean) this.beanDesc.invoke(this.job, "canExecute", null);
		}
		return true;
	}
}
