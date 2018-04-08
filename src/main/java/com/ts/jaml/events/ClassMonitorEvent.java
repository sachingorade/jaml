package com.ts.jaml.events;

import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public abstract class ClassMonitorEvent implements JamlEvent {
	
	protected ClassMonitorInfo classMonitorInfo;

	protected ClassMonitorEvent(ClassMonitorInfo classMonitorInfo) {
		this.classMonitorInfo = classMonitorInfo;
	}

	public ClassMonitorInfo getClassMonitorInfo() {
		return classMonitorInfo;
	}

	public void setClassMonitorInfo(ClassMonitorInfo classMonitorInfo) {
		this.classMonitorInfo = classMonitorInfo;
	}

	@Override
	public String toString() {
		return "ClassMonitorEvent [classMonitorInfo=" + classMonitorInfo + "]";
	}
	
}
