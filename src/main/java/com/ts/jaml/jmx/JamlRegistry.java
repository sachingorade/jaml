package com.ts.jaml.jmx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ts.jaml.App;
import com.ts.jaml.events.ClassMonitorAddedEvent;
import com.ts.jaml.events.ClassMonitorRemovedEvent;
import com.ts.jaml.events.ClassMonitorRetransformedEvent;
import com.ts.jaml.events.ClassMonitorTransformedEvent;
import com.ts.jaml.events.ClassMonitorUntransformedEvent;
import com.ts.jaml.events.ClassMonitorUpdatedEvent;
import com.ts.jaml.events.JamlEvent;
import com.ts.jaml.pojo.ClassMonitorInfo;

/**
 * @author saching
 *
 */
public class JamlRegistry implements JamlRegistryMBean {

	private volatile static JamlRegistry instance;

	protected Map<String, ClassMonitorInfo> classesToMonitor = new HashMap<>();
	
	protected Map<String, ClassMonitorInfo> classesBeingMonitored = new HashMap<>();
	
	protected JamlRegistry () {
	}
	
	public static JamlRegistry getInstance() {
		if (instance == null) {
			synchronized (JamlRegistry.class) {
				if (instance == null) {
					instance = new JamlRegistry();
				}
			}
		}
		return instance;
	}
	
	public synchronized boolean shouldMonitor(String className) {
		return classesToMonitor.containsKey(className);
	}

	public synchronized boolean shouldMonitor(String className, String methodName) {
		ClassMonitorInfo info = classesToMonitor.get(className);
		if (info == null) {
			return false;
		}
		/*
		 * Special case that when no method has been specified in the file or input, we monitor all the methods...
		 * 
		 * This is because before transforming the class we are not known which methods are present in the class
		 * and we don't want to load the class only for finding out the methods to be monitored if user has not
		 * specified any.
		 */
		return info.getMethods() == null || info.getMethods().contains(methodName);
	}
	
	/**
	 * @param className class to check whether it is monitored
	 * @return true if is being monitored
	 */
	public synchronized boolean isCurrentlyMonitored(String className) {
		return classesBeingMonitored.containsKey(className);
	}
	
	public synchronized boolean isMonitored(String className, String methodName) {
		ClassMonitorInfo info = classesBeingMonitored.get(className);
		return (info != null) && (info.getMethods() == null || info.getMethods().contains(methodName));
	}
	
	public synchronized void addClassesToMonitor(String... classNames) {
		for (String className : classNames) {
			JamlEvent event = null;
			ClassMonitorInfo monitorInfo = classesToMonitor.get(className);
			if (monitorInfo == null) {
				monitorInfo = new ClassMonitorInfo(className);
				event = new ClassMonitorAddedEvent(monitorInfo);
			} else {
				event = new ClassMonitorUpdatedEvent(monitorInfo);
			}
			classesToMonitor.put(className, monitorInfo);
			App.publishEvent(event);
		}
	}

	public synchronized void addClassesToMonitor(Map<String, Set<String>> classes) {
		for (Entry<String, Set<String>> entry : classes.entrySet()) {
			ClassMonitorInfo info = classesToMonitor.get(entry.getKey());
			JamlEvent event;
			if (info == null) {
				info = new ClassMonitorInfo(entry.getKey());
				classesToMonitor.put(entry.getKey(), info);
				event = new ClassMonitorAddedEvent(info);
			} else {
				event = new ClassMonitorUpdatedEvent(info);
			}
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				if (info.getMethods() == null) {
					info.setMethods(new HashSet<String>());
				}
				info.getMethods().addAll(entry.getValue());
			}
			App.logMessage("Monitor changed for : " + info);
			App.publishEvent(event);
		}
	}
	
	public synchronized void addClassBeingMonitored(String... classNames) {
		for (String className : classNames) {
			ClassMonitorInfo classMonitorInfo = new ClassMonitorInfo(className);
			classesBeingMonitored.put(className, classMonitorInfo);
			App.publishEvent(new ClassMonitorTransformedEvent(classMonitorInfo));
		}
	}
	
	public synchronized void addClassBeingMonitored(Map<String, Set<String>> classes) {
		for (Entry<String, Set<String>> entry : classes.entrySet()) {
			ClassMonitorInfo info = classesBeingMonitored.get(entry.getKey());
			JamlEvent event;
			if (info == null) {
				info = new ClassMonitorInfo(entry.getKey());
				classesBeingMonitored.put(entry.getKey(), info);
				event = new ClassMonitorTransformedEvent(info);
			} else {
				event = new ClassMonitorRetransformedEvent(info);
			}
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				if (info.getMethods() == null) {
					info.setMethods(new HashSet<String>());
				}
				info.getMethods().addAll(entry.getValue());
			}
			App.publishEvent(event);
		}
	}

	@Override
	public synchronized void removeClassToMonitor(String className) {
		removeClassesToMonitor(className);
	}
	
	public synchronized void removeClassesToMonitor(String... classNames) {
		for(String className : classNames) {
			ClassMonitorInfo classMonitorInfo = classesToMonitor.remove(className);
			if (classMonitorInfo != null) {
				App.publishEvent(new ClassMonitorRemovedEvent(classMonitorInfo));
			}
		}
	}
	
	public synchronized void removeClassesBeingMonitored(String... classes) {
		for(String className : classes) {
			ClassMonitorInfo classMonitorInfo = classesBeingMonitored.remove(className);
			if (classMonitorInfo != null) {
				App.publishEvent(new ClassMonitorUntransformedEvent(classMonitorInfo));
			}
		}
	}
	
	public synchronized ClassMonitorInfo getClassMonitorInfo(String className) {
		return classesToMonitor.get(className);
	}

	@Override
	public Set<String> getClassesToMonitor() {
		return new HashSet<>(classesToMonitor.keySet());
	}

	public Set<String> getClassesBeingMonitored() {
		return new HashSet<>(classesBeingMonitored.keySet());
	}

	@Override
	public void addClassToMonitor(String className) {
		addClassesToMonitor(className);
	}

	@Override
	public synchronized void addClassMethodToMonitor(String className, String methodName) {
		Map<String, Set<String>> classesToMonitor = new HashMap<>();
		HashSet<String> methodsToMonitor = new HashSet<>();
		methodsToMonitor.add(methodName);
		classesToMonitor.put(className, methodsToMonitor);
		addClassesToMonitor(classesToMonitor);
	}

	@Override
	public synchronized void removeClassMethodToMonitor(String className, String methodName) {
		ClassMonitorInfo classMonitorInfo = classesToMonitor.get(className);
		if (classMonitorInfo == null) {
			// class is not added to get monitored so returning...
			return;
		}
		classesBeingMonitored.remove(className);
		if (classMonitorInfo.getMethods().remove(methodName)) {
			JamlEvent event = new ClassMonitorUpdatedEvent(classMonitorInfo);
			App.publishEvent(event);
		}
	}

}
