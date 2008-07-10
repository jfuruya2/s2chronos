package org.seasar.chronos.core.task.impl;

import java.lang.annotation.Annotation;

import org.seasar.chronos.core.TaskTrigger;
import org.seasar.chronos.core.task.TaskConstant;
import org.seasar.chronos.core.task.TaskTriggerFinder;
import org.seasar.chronos.core.task.TaskAnnotationReader.TriggerAnnotationHandler;
import org.seasar.framework.convention.NamingConvention;
import org.seasar.framework.util.tiger.ReflectionUtil;

public class TaskTriggerFinderImpl implements TaskTriggerFinder{
	
	private static final String NAME_SPACE_ORG_SEASAR_CHRONOS_CORE = "org.seasar.chronos.core";
	private static final String NAME_SPACE_TRIGGER_C = ".trigger.C";

	private NamingConvention namingConvention;
	
	public void setNamingConvention(NamingConvention namingConvention) {
		this.namingConvention = namingConvention;
	}
	
	private Class<?> getTriggerAnnotationClass(String packageName,
			String className) {
		StringBuilder sb = new StringBuilder(packageName);
		sb.append(NAME_SPACE_TRIGGER_C);
		sb.append(className);
		Class<?> triggerClass = ReflectionUtil
				.forNameNoException(sb.toString());
		return triggerClass;
	}
	
	public Class<?> findTriggerAnnotationClassForRootPackages(
			String annotationName) {
		Class<?> result = null;
		for (String packageName : this.namingConvention.getRootPackageNames()) {
			result = this
					.getTriggerAnnotationClass(packageName, annotationName);
			if (result != null) {
				return result;
			}
		}
		return result;
	}
	
	public TaskTrigger find(Annotation[] annotations,TriggerAnnotationHandler triggerAnnotationHandler){
		for (Annotation annotation : annotations) {
			Class<?> annotaionClass = annotation.annotationType();
			String annotationName = annotaionClass.getSimpleName();
			// サフィックスがTriggerなアノテーションを検索する
			if (annotationName.endsWith(TaskConstant.NAME_SPACE_TRIGGER_ANNOTATION_SUFFIX)) {
				Class<?> triggerAnnotationClass = this
						.getTriggerAnnotationClass(
								NAME_SPACE_ORG_SEASAR_CHRONOS_CORE,
								annotationName);
				// 標準パッケージで見つからないなら、rootPackageから検索してみる
				if (triggerAnnotationClass == null) {
					triggerAnnotationClass = this
							.findTriggerAnnotationClassForRootPackages(annotationName);
				}
				TaskTrigger taskTrigger = triggerAnnotationHandler.process(
						annotation, triggerAnnotationClass);
				if (taskTrigger != null) {
					return taskTrigger;
				}
			}
		}
		return null;
	}
}