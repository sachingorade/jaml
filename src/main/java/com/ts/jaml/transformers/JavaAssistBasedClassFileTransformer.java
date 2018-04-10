package com.ts.jaml.transformers;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import com.ts.jaml.App;
import com.ts.jaml.jmx.JamlRegistry;
import com.ts.jaml.pojo.ClassMonitorInfo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author saching
 *
 */
public class JavaAssistBasedClassFileTransformer extends JamlClassFileTransformer {

	private JamlRegistry jamlRegistry = JamlRegistry.getInstance();
	
	public JavaAssistBasedClassFileTransformer(Instrumentation instrumentation) {
		super(instrumentation);
	}

	@Override
	protected byte[] transformInternal(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		ClassMonitorInfo classMonitorInfo = jamlRegistry.getClassMonitorInfo(className);
		try {
			App.logMessage("Transforming class:" + classMonitorInfo.getClasssName());
			return defineClass(classMonitorInfo, classfileBuffer);
		} catch (Exception e) {
			throw new IllegalClassFormatException(e.getMessage());
		}
	}

	private byte[] defineClass(ClassMonitorInfo classMonitorInfo, byte[] classfileBuffer) throws Exception {
		try {
			ClassPool classPool = ClassPool.getDefault();
			CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
			CtMethod methods[] = ctClass.getDeclaredMethods();
			for (CtMethod m : methods) {
				if (classMonitorInfo.getMethods() == null || classMonitorInfo.getMethods().contains(m.getName())) {
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
			byte[] bytecode = ctClass.toBytecode();
			ctClass.detach();
			return bytecode;
		} catch (Exception e) {
			App.logMessage("Error :[" + e.getMessage() + "] while transforming class:" + classMonitorInfo.getClasssName());
			throw e;
		}
	}

	@Override
	public synchronized void addClassToMonitor(String className) throws ClassNotFoundException, UnmodifiableClassException {
		jamlRegistry.removeClassesBeingMonitored(className);
		reloadClass(className);
	}

	@Override
	public synchronized void removeClassFromMonitoring(String className) throws ClassNotFoundException, UnmodifiableClassException {
		jamlRegistry.removeClassesBeingMonitored(className);
		reloadClass(className);
	}
	
	private void reloadClass(String className) throws UnmodifiableClassException, ClassNotFoundException {
		Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
		for (Class<?> loadedClass : loadedClasses) {
			if (className.equals(loadedClass.getName())) {
				App.logMessage("Retransforming class : " + loadedClass.getName());
				if (instrumentation.isModifiableClass(loadedClass)) {
					try {
						instrumentation.retransformClasses(loadedClass);
					} catch (Exception e) {
						App.logMessage("Error [" + e.getMessage() + "] while retransforming class " + className +", trying to reload the class.");
					}
				} else {
					instrumentation.retransformClasses(loadedClass.getClass().getClassLoader().loadClass(className));
				}
			}
		}
	}
}
