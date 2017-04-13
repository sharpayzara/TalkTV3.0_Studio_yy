package com.sumavision.talktv2.annotation.event;

import android.widget.AdapterView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: wyouflf Date: 13-8-16 Time: 下午2:32
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = AdapterView.OnItemClickListener.class, listenerSetter = "setOnItemClickListener", methodName = "onItemClick")
public @interface OnItemClick {
	int[] value();

	int[] parentId() default 0;
}
