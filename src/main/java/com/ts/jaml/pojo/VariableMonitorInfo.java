/*
 * Name: VariableMonitorInfo.java
 *
 * Created by saching on Apr 22, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.pojo;

import java.io.Serializable;

/**
 * @author saching
 *
 */
public class VariableMonitorInfo implements Serializable, Comparable<VariableMonitorInfo> {

	private static final long serialVersionUID = 1L;

	private String variable;
	private int lineNumber;
	
	public VariableMonitorInfo() {
	}

	/**
	 * @param variable
	 * @param lineNumber
	 */
	public VariableMonitorInfo(String variable, int lineNumber) {
		super();
		this.variable = variable;
		this.lineNumber = lineNumber;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "VariableMonitorInfo [variable=" + variable + ", lineNumber=" + lineNumber + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lineNumber;
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableMonitorInfo other = (VariableMonitorInfo) obj;
		if (lineNumber != other.lineNumber)
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

	@Override
	public int compareTo(VariableMonitorInfo o) {
		if (o == null) {
			return Integer.valueOf(lineNumber);
		}
		return Integer.compare(lineNumber, o.lineNumber);
	}
	
}
