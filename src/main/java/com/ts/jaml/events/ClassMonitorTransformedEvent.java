/*
 * Name: ClassMonitorStartedEvent.java
 *
 * Created by saching on Apr 3, 2018
 *
 * Description: Event which notifies that class has been transformed
 *
 */
package com.ts.jaml.events;

import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public class ClassMonitorTransformedEvent extends ClassMonitorEvent implements JamlEvent {

	public ClassMonitorTransformedEvent(ClassMonitorInfo classMonitorInfo) {
		super(classMonitorInfo);
	}

}
