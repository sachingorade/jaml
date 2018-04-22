/*
 * Name: AbstractMethodMonitorInfo.java
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
public abstract class AbstractMethodMonitorInfo implements MethodMonitorInfo {

	private static final long serialVersionUID = 1L;
	
	protected String methodName;
	
	public AbstractMethodMonitorInfo(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
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
		AbstractMethodMonitorInfo other = (AbstractMethodMonitorInfo) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}
	
}
