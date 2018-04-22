/*
 * Name: InvocationCounterMonitorInfo.java
 *
 * Created by saching on Apr 12, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.pojo;

/**
 * @author saching
 *
 */
public class InvocationCounterMonitorInfo extends AbstractMethodMonitorInfo implements MethodMonitorInfo {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param methodName
	 */
	public InvocationCounterMonitorInfo(String methodName) {
		super(methodName);
	}
	
}
