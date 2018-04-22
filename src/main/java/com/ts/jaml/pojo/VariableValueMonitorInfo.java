/*
 * Name: VariableValueMonitorInfo.java
 *
 * Created by saching on Apr 12, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.pojo;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author saching
 *
 */
public class VariableValueMonitorInfo extends AbstractMethodMonitorInfo implements MethodMonitorInfo {

	private static final long serialVersionUID = 1L;
	
	private Set<VariableMonitorInfo> variableMonitorInfos;
	
	public VariableValueMonitorInfo(String methodName) {
		super(methodName);
		variableMonitorInfos = new TreeSet<>();
	}

	/**
	 * @param variableName
	 * @param lineNumber
	 */
	public VariableValueMonitorInfo(String methodName, String variableName, Integer lineNumber) {
		super(methodName);
		variableMonitorInfos = new TreeSet<>();
		variableMonitorInfos.add(new VariableMonitorInfo(variableName, lineNumber));
	}

	public Set<VariableMonitorInfo> getVariableMonitorInfos() {
		return variableMonitorInfos;
	}

	public void setVariableMonitorInfos(Set<VariableMonitorInfo> variableMonitorInfos) {
		this.variableMonitorInfos = variableMonitorInfos;
	}

	@Override
	public String toString() {
		return "VariableValueMonitorInfo [variableMonitorInfos=" + variableMonitorInfos + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((variableMonitorInfos == null) ? 0 : variableMonitorInfos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableValueMonitorInfo other = (VariableValueMonitorInfo) obj;
		if (variableMonitorInfos == null) {
			if (other.variableMonitorInfos != null)
				return false;
		} else if (!variableMonitorInfos.equals(other.variableMonitorInfos))
			return false;
		return true;
	}

}
