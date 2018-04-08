/*
 * Name: JamlRegistryMNBean.java
 *
 * Created by saching on Apr 8, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.jmx;

import java.util.Set;

/**
 * @author saching
 *
 */
public interface JamlRegistryMBean {

	/**
	 * @param className class to monitor
	 */
	void addClassToMonitor(String className);

	/**
	 * @param className class to monitor
	 * @param methodName method from the class to monitor
	 */
	void addClassMethodToMonitor(String className, String methodName);

	/**
	 * @param classNames class to remove from monitoring
	 */
	void removeClassToMonitor(String className);

	/**
	 * @param className class to monitor
	 * @param methodName method from the class to monitor
	 */
	void removeClassMethodToMonitor(String className, String methodName);

	/**
	 * @return returns the classes which are to be monitored
	 */
	Set<String> getClassesToMonitor();

}
