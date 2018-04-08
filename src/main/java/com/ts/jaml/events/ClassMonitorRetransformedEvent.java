/*
 * Name: ClassMonitorRetransformedEvent.java
 *
 * Created by saching on Apr 3, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.events;

import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public class ClassMonitorRetransformedEvent extends ClassMonitorEvent implements JamlEvent {

	public ClassMonitorRetransformedEvent(ClassMonitorInfo classMonitorInfo) {
		super(classMonitorInfo);
	}

}
