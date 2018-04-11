package com.ts.jaml.transformers;

import java.io.ByteArrayInputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import com.ts.jaml.App;
import com.ts.jaml.jmx.JamlRegistry;
import com.ts.jaml.pojo.ClassMonitorInfo;
import com.ts.jaml.pojo.ExecutionTimeMonitorInfo;
import com.ts.jaml.pojo.MethodMonitorInfo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author saching
 *
 */
public class JavaAssistBasedClassFileTransformer extends JamlClassFileTransformer {

	private JamlRegistry jamlRegistry = JamlRegistry.getInstance();
	
	private static final MethodMonitorInfo DEFAULT_METHOD_MONITOR_INFO = new ExecutionTimeMonitorInfo();
	
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
				MethodMonitorInfo methodMonitorInfo = classMonitorInfo.getMethodsToMonitor()  == null ? DEFAULT_METHOD_MONITOR_INFO : classMonitorInfo.getMethodsToMonitor().get(m.getName());
				transformMethod(m, methodMonitorInfo, ctClass);
			}
			byte[] bytecode = ctClass.toBytecode();
			ctClass.detach();
			return bytecode;
		} catch (Exception e) {
			App.logMessage("Error :[" + e.getMessage() + "] while transforming class:" + classMonitorInfo.getClasssName());
			throw e;
		}
	}

	/**
	 * @param method method to transform
	 * @param methodMonitorInfo method monitoring info
	 * @param ctClass class to which method belongs
	 * @throws Exception thrown in case of failure
	 */
	private void transformMethod(CtMethod method, MethodMonitorInfo methodMonitorInfo, CtClass ctClass) throws Exception {
		if (methodMonitorInfo instanceof ExecutionTimeMonitorInfo) {
			TransformationHelper.addExecutionTimeMonitorInfo(method, ctClass);
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
