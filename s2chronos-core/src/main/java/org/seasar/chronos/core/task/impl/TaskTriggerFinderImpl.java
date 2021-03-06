/*
 * Copyright 2007-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.chronos.core.task.impl;

import java.lang.annotation.Annotation;

import org.seasar.chronos.core.model.TaskTrigger;
import org.seasar.chronos.core.task.TaskConstant;
import org.seasar.chronos.core.task.TaskTriggerFinder;
import org.seasar.chronos.core.task.TaskAnnotationReader.TriggerAnnotationHandler;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.tiger.ReflectionUtil;

/**
 * {@link TaskTriggerFinder}の実装です。
 * 
 * @author j5ik2o
 */
public class TaskTriggerFinderImpl implements TaskTriggerFinder {
	private static final String NAME_SPACE_ORG_SEASAR_CHRONOS_CORE_MODEL =
		"org.seasar.chronos.core.model";

	private static final String NAME_SPACE_TRIGGER_C = ".trigger.C";

	private NamingConvention namingConvention;

	/**
	 * {@link NamingConvention}を設定します。
	 * 
	 * @param namingConvention
	 *            {@link NamingConvention}
	 */
	public void setNamingConvention(NamingConvention namingConvention) {
		this.namingConvention = namingConvention;
	}

	private Class<?> getTriggerAnnotationClass(String packageName,
			String className) {
		StringBuilder sb = new StringBuilder(packageName);
		sb.append(NAME_SPACE_TRIGGER_C);
		sb.append(className);
		Class<?> triggerClass =
			ReflectionUtil.forNameNoException(sb.toString());
		return triggerClass;
	}

	/**
	 * ルートパッケージからトリガーアノテーションクラスを検索します。
	 * 
	 * @param annotationName
	 *            アノテーション名
	 * @return トリガーアノテーションクラス
	 */
	public Class<?> findTriggerAnnotationClassForRootPackages(
			String annotationName) {
		Class<?> result = null;
		for (String packageName : this.namingConvention.getRootPackageNames()) {
			result = getTriggerAnnotationClass(packageName, annotationName);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * org.seasar.chronos.core.task.TaskTriggerFinder#find(java.lang.annotation
	 * .Annotation[],
	 * org.seasar.chronos.core.task.TaskAnnotationReader.TriggerAnnotationHandler
	 * )
	 */
	public TaskTrigger find(Annotation[] annotations,
			TriggerAnnotationHandler triggerAnnotationHandler) {
		for (Annotation annotation : annotations) {
			Class<?> annotaionClass = annotation.annotationType();
			String annotationName = annotaionClass.getSimpleName();
			// サフィックスがTriggerなアノテーションを検索する
			if (annotationName
				.endsWith(TaskConstant.NAME_SPACE_TRIGGER_ANNOTATION_SUFFIX)) {
				Class<?> triggerAnnotationClass =
					this.getTriggerAnnotationClass(
						NAME_SPACE_ORG_SEASAR_CHRONOS_CORE_MODEL,
						annotationName);
				// 標準パッケージで見つからないなら、rootPackageから検索してみる
				if (triggerAnnotationClass == null) {
					triggerAnnotationClass =
						this
							.findTriggerAnnotationClassForRootPackages(annotationName);
				}
				TaskTrigger taskTrigger =
					triggerAnnotationHandler.process(
						annotation,
						triggerAnnotationClass);
				if (taskTrigger != null) {
					return taskTrigger;
				}
			}
		}
		return null;
	}
}
