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
import javassist.bytecode.LocalVariableAttribute;

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
		ClassPool classPool = ClassPool.getDefault();
		CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
		CtMethod methods[] = ctClass.getDeclaredMethods();
		for (CtMethod m : methods) {
			if (classMonitorInfo.getMethods() == null || classMonitorInfo.getMethods().contains(m.getName())) {
				App.logMessage("Updating method:" + m.getName() + "," + m.getSignature() + "," + m.getDeclaringClass().getName());
				String varName = "jaml_" + m.getName() + "_startTime";
				m.addLocalVariable(varName, CtClass.longType);
				m.insertBefore(varName + " = System.currentTimeMillis();");
				m.insertAfter("System.out.println(\"[JAML] [" + ctClass.getName() + ":" + m.getName() + "] - Execution Duration "
						+ "(milli sec): \"+ (System.currentTimeMillis() - " + varName + ") );");
				m.insertAfter("System.out.print(\"[JAML][Arguments]:\");");
				m.insertAfter("System.out.println($args.length);");
				m.insertAfter("for(int i=0;i<$args.length;i++) {System.out.println($args[i]);}");
			}
		}
		byte[] bytecode = ctClass.toBytecode();
		ctClass.detach();
		return bytecode;
	}

	@Override
	public synchronized void addClassToMonitor(String className) throws ClassNotFoundException, UnmodifiableClassException {
		jamlRegistry.removeClassesBeingMonitored(className);
		instrumentation.retransformClasses(Class.forName(className));
	}

	@Override
	public synchronized void removeClassFromMonitoring(String className) throws ClassNotFoundException, UnmodifiableClassException {
		jamlRegistry.removeClassesBeingMonitored(className);
		instrumentation.retransformClasses(Class.forName(className));
	} 
}
