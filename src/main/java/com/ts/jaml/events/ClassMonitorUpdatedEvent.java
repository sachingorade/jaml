package com.ts.jaml.events;

import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public class ClassMonitorUpdatedEvent extends ClassMonitorEvent implements JamlEvent {

	public ClassMonitorUpdatedEvent(ClassMonitorInfo classMonitorInfo) {
		super(classMonitorInfo);
	}

}
