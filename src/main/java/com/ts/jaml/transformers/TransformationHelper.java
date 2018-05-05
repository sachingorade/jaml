/*
 * Name: TransformationHelper.java
 *
 * Created by saching on Apr 11, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.transformers;

import java.util.Set;

import com.ts.jaml.App;
import com.ts.jaml.pojo.VariableMonitorInfo;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * @author saching
 *
 */
public class TransformationHelper {

	public static void addExecutionTimeMonitorInfo(CtMethod m, CtClass ctClass, boolean printArguments) throws Exception{
		App.logMessage("Updating method:" + m.getName() + "," + m.getSignature() + "," + m.getDeclaringClass().getName());
		String startTimeVarName = "jaml_" + m.getName() + "_startTime";
		String endTimeVarName = "jaml_" + m.getName() + "_endTime";
		m.addLocalVariable(startTimeVarName, CtClass.longType);
		m.addLocalVariable(endTimeVarName, CtClass.longType);
		
		/*
		 * Record start time
		 */
		m.insertBefore(startTimeVarName + " = System.currentTimeMillis();");

		StringBuilder afterCode = new StringBuilder();
		/*
		 * Record end time
		 */
		afterCode.append(endTimeVarName + " = System.currentTimeMillis();");
		
		String argVarName = "jaml_" + m.getName() + "_args";
		afterCode.append("StringBuilder " + argVarName + " = new StringBuilder();");
		if (printArguments) {
			afterCode.append(argVarName + ".append(\":Arguments:\");");
			afterCode.append("for(int i=0;i<$args.length;i++) {if(i>0) {" + argVarName + ".append(\",\");}" + argVarName + ".append($args[i]);}");
		}
		
		afterCode.append("System.out.println(\"[JAML] [\" + Thread.currentThread().getName() + \"] [Execution:" + ctClass.getName() + ":" + m.getName() + ":\" + "
				+ startTimeVarName + " + \":\" + " + endTimeVarName + " + \":\" + (" + endTimeVarName + " - " + startTimeVarName + ") + " + argVarName  + " + \"]\");");
		
		m.insertAfter(afterCode.toString());
	}

	/**
	 * Injects method execution counter to a class
	 * @param method method to add invocation counter for
	 * @param ctClass class to which this method belongs
	 * @throws Exception thrown in case of failure
	 */
	public static void addInvocationCounterMonitorInfo(CtMethod m, CtClass ctClass) throws Exception {
		App.logMessage("Updating class:" + ctClass.getName() + ", adding method counter for:" + m.getName() + "," + m.getSignature() + "," + m.getDeclaringClass().getName());
		String counterVariableName = "jaml_method_" + m.getName() + "_invocation_counter";
		CtField staticMethodCounterField = new CtField(CtClass.longType, counterVariableName, ctClass);
		staticMethodCounterField.setModifiers(Modifier.STATIC | Modifier.PRIVATE | Modifier.VOLATILE);
		ctClass.addField(staticMethodCounterField);
		
		/*
		 * Record and print method execution invocation counter
		 */
		m.insertBefore(counterVariableName + " = " + counterVariableName + " + 1;");
		m.insertAfter("System.out.println(\"[JAML] [\" + Thread.currentThread().getName() + \"] [Invocation:" + ctClass.getName() + ":" + m.getName() + ":\" + " + counterVariableName + " + \"]\");");
	}

	/**
	 * Injects the code to log variable value at specified line
	 * @param method method in which debug info is to be injected
	 * @param ctClass class to which this method belongs
	 * @param variableMonitorInfos info about the variables to be logged
	 * @throws Exception thrown in case of failure
	 */
	public static void addVariableValueMonitorInfo(CtMethod m, CtClass ctClass, Set<VariableMonitorInfo> variableMonitorInfos) throws Exception {
		int variableLineDelta = 0;
		for (VariableMonitorInfo info : variableMonitorInfos) {
			String variableName = info.getVariable();
			int lineNumber = info.getLineNumber();
			App.logMessage("Updating class:" + ctClass.getName() + ", adding variable monitor for:" + m.getName() + ":" + variableName + "," + m.getSignature() + "," + m.getDeclaringClass().getName());
			m.insertAt(lineNumber + variableLineDelta, "System.out.println(\"[JAML] [\" + Thread.currentThread().getName() + \"] [Variable:" + ctClass.getName() + ":" + m.getName() + ":" + variableName + ":\" + " + variableName + " + \"]\");");
			variableLineDelta++;
		}
	}
	
}
