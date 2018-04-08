/*
 * Name: ClassMonitorUntransformedEvent.java
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
public class ClassMonitorUntransformedEvent extends ClassMonitorEvent implements JamlEvent {

	public ClassMonitorUntransformedEvent(ClassMonitorInfo classMonitorInfo) {
		super(classMonitorInfo);
	}

}
