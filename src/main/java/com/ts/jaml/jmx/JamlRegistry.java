package com.ts.jaml.jmx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ts.jaml.App;
import com.ts.jaml.Utils;
import com.ts.jaml.events.ClassMonitorAddedEvent;
import com.ts.jaml.events.ClassMonitorRemovedEvent;
import com.ts.jaml.events.ClassMonitorRetransformedEvent;
import com.ts.jaml.events.ClassMonitorTransformedEvent;
import com.ts.jaml.events.ClassMonitorUntransformedEvent;
import com.ts.jaml.events.ClassMonitorUpdatedEvent;
import com.ts.jaml.events.JamlEvent;
import com.ts.jaml.pojo.ClassMonitorInfo;
import com.ts.jaml.pojo.MethodMonitorInfo;

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
		return info.getMethodsToMonitor() == null || info.getMethodsToMonitor().containsKey(methodName);
	}
	
	/**
	 * @param className class to check whether it is monitored
	 * @return true if is being monitored
	 */
	public synchronized boolean isCurrentlyMonitored(String className) {
		return classesBeingMonitored.containsKey(className);
	}
	
	public synchronized boolean isClassMethodBeingMonitored(String className, String methodName) {
		ClassMonitorInfo info = classesBeingMonitored.get(className);
		return (info != null) && (info.getMethodsToMonitor() == null || info.getMethodsToMonitor().containsKey(methodName));
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

	public synchronized void addClassToMonitor(ClassMonitorInfo classMonitorInfo) {
		if (classMonitorInfo == null) {
			return;
		}
		ClassMonitorInfo monitorInfo = classesToMonitor.get(classMonitorInfo.getClasssName());
		JamlEvent event = null;
		if (monitorInfo != null) {
			if (monitorInfo.getMethodsToMonitor() != null) {
				monitorInfo.getMethodsToMonitor().putAll(classMonitorInfo.getMethodsToMonitor());
			} else {
				monitorInfo.setMethods(classMonitorInfo.getMethodsToMonitor());
			}
			event = new ClassMonitorUpdatedEvent(monitorInfo);
			classesToMonitor.put(classMonitorInfo.getClasssName(), monitorInfo);
		} else {
			event = new ClassMonitorAddedEvent(classMonitorInfo);
			classesToMonitor.put(classMonitorInfo.getClasssName(), classMonitorInfo);
		}
		App.logMessage("Monitor changed for : " + classMonitorInfo);
		App.publishEvent(event);
	}
	
	public synchronized void addClassesToMonitor(Map<String, Map<String, MethodMonitorInfo>> classes) {
		for (Entry<String, Map<String, MethodMonitorInfo>> entry : classes.entrySet()) {
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
				if (info.getMethodsToMonitor() == null) {
					info.setMethods(new HashMap<String, MethodMonitorInfo>());
				}
				info.getMethodsToMonitor().putAll(entry.getValue());
			}
			App.logMessage("Monitor changed for : " + info);
			App.publishEvent(event);
		}
	}
	
	public synchronized void addClassBeingMonitored(String... classNames) {
		for (String className : classNames) {
			if (!classesToMonitor.containsKey(className)) {
				throw new IllegalArgumentException("Class:[" + className + "] is not present in monitor registry.");
			}
			ClassMonitorInfo classMonitorInfo = new ClassMonitorInfo(className);
			classesBeingMonitored.put(className, classMonitorInfo);
			App.publishEvent(new ClassMonitorTransformedEvent(classMonitorInfo));
		}
	}
	
	public synchronized void addClassBeingMonitored(Map<String, Map<String, MethodMonitorInfo>> classes) {
		for (Entry<String, Map<String, MethodMonitorInfo>> entry : classes.entrySet()) {
			if (!classesToMonitor.containsKey(entry.getKey())) {
				throw new IllegalArgumentException("Class:[" + entry.getKey() + "] is not present in monitor registry.");
			}
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
				if (info.getMethodsToMonitor() == null) {
					info.setMethods(new HashMap<String, MethodMonitorInfo>());
				}
				info.getMethodsToMonitor().putAll(entry.getValue());
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
	public Set<String> getClassesToBeMonitored() {
		return new HashSet<>(classesToMonitor.keySet());
	}

	public Set<String> getClassesCurrentlyBeingMonitored() {
		return new HashSet<>(classesBeingMonitored.keySet());
	}

	@Override
	public void addClassToMonitor(String className) {
		addClassesToMonitor(className);
	}

	@Override
	public synchronized void addClassMethodToMonitor(String className, String methodName) {
		ClassMonitorInfo info = new ClassMonitorInfo(className);
		MethodMonitorInfo methodMonitorInfo = Utils.getMethodMonitorInfoFromString(methodName);
		if (methodMonitorInfo != null) {
			Map<String, MethodMonitorInfo> methodsToMonitor = new HashMap<>();
			methodsToMonitor.put(methodMonitorInfo.getMethodName(), methodMonitorInfo);
			info.setMethods(methodsToMonitor);
		}
		addClassToMonitor(info);
	}

	@Override
	public synchronized void removeClassMethodToMonitor(String className, String methodName) {
		ClassMonitorInfo classMonitorInfo = classesToMonitor.get(className);
		if (classMonitorInfo == null) {
			// class is not added to get monitored so returning...
			return;
		}
		classesBeingMonitored.remove(className);
		MethodMonitorInfo methodMonitorInfo = classMonitorInfo.getMethodsToMonitor() == null ? null : classMonitorInfo.getMethodsToMonitor().remove(methodName);
		if (classMonitorInfo.getMethodsToMonitor() == null || (classMonitorInfo.getMethodsToMonitor().isEmpty() && methodMonitorInfo != null)) {
			classesToMonitor.remove(className);
			JamlEvent event = new ClassMonitorRemovedEvent(classMonitorInfo);
			App.publishEvent(event);
			return;
		}
		JamlEvent event = new ClassMonitorUpdatedEvent(classMonitorInfo);
		App.publishEvent(event);
	}

	Map<String, ClassMonitorInfo> getClassesToMonitor() {
		return classesToMonitor;
	}

	Map<String, ClassMonitorInfo> getClassesBeingMonitored() {
		return classesBeingMonitored;
	}

}
