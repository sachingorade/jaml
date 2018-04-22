/*
 * Name: ExecutionTimeMonitorInfo.java
 *
 * Created by saching on Apr 11, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.pojo;

/**
 * @author saching
 *
 */
public class ExecutionTimeMonitorInfo extends AbstractMethodMonitorInfo implements MethodMonitorInfo {

	private static final long serialVersionUID = 1L;
	
	private boolean printArguments;
	
	public ExecutionTimeMonitorInfo(String methodName) {
		this(methodName, false);
	}

	public ExecutionTimeMonitorInfo(String methodName, boolean printArguments) {
		super(methodName);
		this.printArguments = printArguments;
	}

	public boolean isPrintArguments() {
		return printArguments;
	}

	public void setPrintArguments(boolean printArguments) {
		this.printArguments = printArguments;
	}

	@Override
	public String toString() {
		return "ExecutionTimeMonitorInfo [printArguments=" + printArguments + "]";
	}
	
}
