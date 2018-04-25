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
	
	public static final Pattern METHOD_INFO_PARAM_NONE = Pattern.compile("([a-zA-Z_$]+[a-zA-Z0-9_$]*){1}");
	public static final Pattern METHOD_INFO_PARAM_EXEC = Pattern.compile("(exec" + MONITOR_TYPE_SEPARATOR + "[a-zA-Z_$]+[0-9a-zA-Z_$]*(\\(printVars\\)){0,1}){1}");
	public static final Pattern METHOD_INFO_PARAM_VAR = Pattern.compile("(var" + MONITOR_TYPE_SEPARATOR + "[a-zA-Z_$]+[0-9a-zA-Z_$]*\\" 
			+ VAR_INFO_START_SEPARATOR +"([a-zA-Z_$]+[a-zA-Z0-9_$]*\\" + VAR_INFO_LINE_NUMBER_SEPARATOR + "[0-9]+\\|*)+\\" + VAR_INFO_END_SEPARATOR +"){1}");
	public static final Pattern METHOD_INFO_PARAM_INVOC = Pattern.compile("(invoc" + MONITOR_TYPE_SEPARATOR + "[a-zA-Z_$]+[0-9a-zA-Z_$]*){1}");
	
	public static MethodMonitorInfo getMethodMonitorInfoFromString(String methodName) {
		if (methodName != null && !methodName.isEmpty()) {
			if (METHOD_INFO_PARAM_EXEC.matcher(methodName).matches()) {
				App.logMessage("Exec method match:" + methodName);
				boolean printVars = false;
				if (methodName.indexOf("(") > 0) {
					methodName = methodName.substring(methodName.indexOf(MONITOR_TYPE_SEPARATOR) + 1, methodName.indexOf("("));
					printVars = true;
				} else {
					methodName = methodName.substring(methodName.indexOf(MONITOR_TYPE_SEPARATOR) + 1, methodName.length());
				}
				return new ExecutionTimeMonitorInfo(methodName, printVars);
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
			} else if (METHOD_INFO_PARAM_NONE.matcher(methodName).matches()) {
				App.logMessage("No method match:" + methodName);
				return new ExecutionTimeMonitorInfo(methodName);
			}
		}
		return null;
	}
	
	public static ClassMonitorInfo getClassMonitorInfoFromString(String input) {
		if (input == null || input.trim().isEmpty()) {
			return null;
		}
		ClassMonitorInfo info = null;
		String[] inputParts = input.split("=");
		String className = inputParts[0].trim();
		info = new ClassMonitorInfo(className);
		if (inputParts.length > 1) {
			String[] parts = inputParts[1].split(",");
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
