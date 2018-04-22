package com.ts.jaml.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author saching
 *
 */
public class ClassMonitorInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String classsName;
	private Map<String, MethodMonitorInfo> methodsToMonitor;
	
	public ClassMonitorInfo(String className) {
		classsName = className;
	}
	
	public String getClasssName() {
		return classsName;
	}
	public void setClasssName(String classsName) {
		this.classsName = classsName;
	}
	public Map<String, MethodMonitorInfo> getMethodsToMonitor() {
		return methodsToMonitor;
	}
	public void setMethods(Map<String, MethodMonitorInfo> methods) {
		this.methodsToMonitor = methods;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classsName == null) ? 0 : classsName.hashCode());
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
		ClassMonitorInfo other = (ClassMonitorInfo) obj;
		if (classsName == null) {
			if (other.classsName != null)
				return false;
		} else if (!classsName.equals(other.classsName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClassMonitorInfo [classsName=" + classsName + ", methods=" + methodsToMonitor + "]";
	}
	
	public ClassMonitorInfo copy() {
		ClassMonitorInfo info = new ClassMonitorInfo(classsName);
		if (methodsToMonitor != null) {
			info.setMethods(new HashMap<String, MethodMonitorInfo>(methodsToMonitor));
		}
		return info;
	}

}
