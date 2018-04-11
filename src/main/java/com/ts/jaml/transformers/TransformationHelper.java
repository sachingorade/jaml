/*
 * Name: TransformationHelper.java
 *
 * Created by saching on Apr 11, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.transformers;

import com.ts.jaml.App;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author saching
 *
 */
public class TransformationHelper {

	public static void addExecutionTimeMonitorInfo(CtMethod m, CtClass ctClass) throws Exception{
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
		afterCode.append("StringBuilder " + argVarName + " = new StringBuilder(\":Arguments:\");");
		afterCode.append("for(int i=0;i<$args.length;i++) {if(i>0) {" + argVarName + ".append(\",\");}" + argVarName + ".append($args[i]);}");
		
		afterCode.append("System.out.println(\"[JAML] [Execution:" + ctClass.getName() + ":" + m.getName() + ":\" + "
				+ startTimeVarName + " + \":\" + " + endTimeVarName + " + \":\" + (" + endTimeVarName + " - " + startTimeVarName + ") + " + argVarName  + " + \"]\");");
		
		m.insertAfter(afterCode.toString());
	}
	
}
