/*
 * Name: Utils.java
 *
 * Created by saching on Apr 20, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.ts.jaml.pojo.ClassMonitorInfo;
import com.ts.jaml.pojo.ExecutionTimeMonitorInfo;
import com.ts.jaml.pojo.InvocationCounterMonitorInfo;
import com.ts.jaml.pojo.MethodMonitorInfo;
import com.ts.jaml.pojo.VariableMonitorInfo;
import com.ts.jaml.pojo.VariableValueMonitorInfo;

/**
 * @author saching
 *
 */
public class Utils {

	public static final String VAR_INFO_SEPARATOR = "|";
	public static final String VAR_INFO_LINE_NUMBER_SEPARATOR = "-";
	public static final String VAR_INFO_START_SEPARATOR = "(";
	public static final String VAR_INFO_END_SEPARATOR = ")";
	public static final String MONITOR_TYPE_SEPARATOR = ":";
	
	public static final Pattern METHOD_INFO_PARAM_EXEC = Pattern.compile("exec" + MONITOR_TYPE_SEPARATOR + "[a-zA-Z]+[0-9a-zA-Z]*");
	public static final Pattern METHOD_INFO_PARAM_VAR = Pattern.compile("var" + MONITOR_TYPE_SEPARATOR + "[a-zA-Z]+[0-9a-zA-Z]*\\" 
			+ VAR_INFO_START_SEPARATOR +"([a-zA-Z]+[a-zA-Z0-9]*\\" + VAR_INFO_LINE_NUMBER_SEPARATOR + "[0-9]+\\|*)+\\" + VAR_INFO_END_SEPARATOR);
	public static final Pattern METHOD_INFO_PARAM_INVOC = Pattern.compile("invoc" + MONITOR_TYPE_SEPARATOR + "[a-zA-Z]+[0-9a-zA-Z]*");
	
	public static MethodMonitorInfo getMethodMonitorInfoFromString(String methodName) {
		if (methodName != null && !methodName.isEmpty()) {
			if (METHOD_INFO_PARAM_EXEC.matcher(methodName).matches()) {
				App.logMessage("Exec method match:" + methodName);
				methodName = methodName.substring(methodName.indexOf(MONITOR_TYPE_SEPARATOR) + 1, methodName.length());
				return new ExecutionTimeMonitorInfo(methodName);
			} else if (METHOD_INFO_PARAM_VAR.matcher(methodName).matches()) {
				App.logMessage("Var method match:" + methodName);
				String variables = methodName.substring(methodName.indexOf(VAR_INFO_START_SEPARATOR) + 1, methodName.indexOf(VAR_INFO_END_SEPARATOR));
				StringTokenizer tokenizer = new StringTokenizer(variables, "|");
				App.logMessage("Fetching method Name from : " + methodName);
				methodName = methodName.substring(methodName.indexOf(MONITOR_TYPE_SEPARATOR) + 1, methodName.indexOf(VAR_INFO_START_SEPARATOR));
				VariableValueMonitorInfo valueMonitorInfo = new VariableValueMonitorInfo(methodName);
				while (tokenizer.hasMoreTokens()) {
					String[] tokens = tokenizer.nextToken().split(VAR_INFO_LINE_NUMBER_SEPARATOR);
					String varName = tokens[0].trim();
					int lineNumber = Integer.valueOf(tokens[1].trim());
					valueMonitorInfo.getVariableMonitorInfos().add(new VariableMonitorInfo(varName, lineNumber));
				}
				return valueMonitorInfo;
			} else if (METHOD_INFO_PARAM_INVOC.matcher(methodName).matches()) {
				App.logMessage("Invoc method match:" + methodName);
				methodName = methodName.substring(methodName.indexOf(MONITOR_TYPE_SEPARATOR) + 1, methodName.length());
				return new InvocationCounterMonitorInfo(methodName);
			}
			App.logMessage("No method match:" + methodName);
		}
		return null;
	}
	
	public static ClassMonitorInfo getClassMonitorInfoFromString(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}
		ClassMonitorInfo info = null;
		String[] parts = input.split(",");
		String className = parts[0];
		info = new ClassMonitorInfo(className);
		if (parts.length > 1) {
			for (int i=0;i<parts.length;i++) {
				String methodName = parts[i].trim();
				if (!methodName.isEmpty()) {
					MethodMonitorInfo methodMonitorInfo = getMethodMonitorInfoFromString(methodName);
					if (methodMonitorInfo != null) {
						if (info.getMethodsToMonitor() == null) {
							info.setMethods(new HashMap<String, MethodMonitorInfo>());
						}
						info.getMethodsToMonitor().put(methodMonitorInfo.getMethodName(), methodMonitorInfo);
					}
				}
			}
		}
		return info;
	}
	
}
