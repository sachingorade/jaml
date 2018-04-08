package com.ts.jaml.pojo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author saching
 *
 */
public class ClassMonitorInfo {

	private String classsName;
	private Set<String> methods;
	
	public ClassMonitorInfo(String className) {
		classsName = className;
	}
	
	public String getClasssName() {
		return classsName;
	}
	public void setClasssName(String classsName) {
		this.classsName = classsName;
	}
	public Set<String> getMethods() {
		return methods;
	}
	public void setMethods(Set<String> methods) {
		this.methods = methods;
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
		return "ClassMonitorInfo [classsName=" + classsName + ", methods=" + methods + "]";
	}
	
	public ClassMonitorInfo copy() {
		ClassMonitorInfo info = new ClassMonitorInfo(classsName);
		if (methods != null) {
			info.setMethods(new HashSet<>(methods));
		}
		return info;
	}

}
