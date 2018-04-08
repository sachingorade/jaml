package com.ts.jaml.events;

import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public class ClassMonitorAddedEvent extends ClassMonitorEvent implements JamlEvent {

	public ClassMonitorAddedEvent(ClassMonitorInfo classMonitorInfo) {
		super(classMonitorInfo);
	}

}
