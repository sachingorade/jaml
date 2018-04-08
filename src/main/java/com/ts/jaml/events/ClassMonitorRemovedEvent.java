package com.ts.jaml.events;

import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public class ClassMonitorRemovedEvent extends ClassMonitorEvent implements JamlEvent {

	public ClassMonitorRemovedEvent(ClassMonitorInfo classMonitorInfo) {
		super(classMonitorInfo);
	}

}
