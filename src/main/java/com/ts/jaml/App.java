package com.ts.jaml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.google.common.eventbus.EventBus;
import com.ts.jaml.events.JamlEvent;
import com.ts.jaml.factory.TransformerFactory;
import com.ts.jaml.jmx.JamlRegistry;
import com.ts.jaml.pojo.ClassMonitorInfo;
import com.ts.jaml.transformers.JamlClassFileTransformer;

/**
 * JAML entry point
 *
 */
public class App {

	/**
	 * 
	 */
	private static final String CLASSFILE_ARGUMENT = "classfile:";

	private static String agentArgs;
	private static Instrumentation instrumentation;
	private static JamlClassFileTransformer jamlClassFileTransformer;
	private static boolean initialized;
	
	private static final EventBus eventBus = new EventBus();
	private static final JamlRegistry jamlRegistry = JamlRegistry.getInstance();
	
	public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
		synchronized (App.class) {
			if (initialized) {
				throw new RuntimeException("JAML is already enabled, an attempt is made to enable it again.");
			}
			App.agentArgs = agentArgs;
			App.instrumentation = instrumentation;
			initJaml();
			initialized = true;
		}
    }

	private static void initJaml() throws Exception {
		App.logMessage("VM Support for Native Method Prefix Supported:[" + instrumentation.isNativeMethodPrefixSupported() + "]");
		App.logMessage("VM Support for Redefine Classes Supported:[" + instrumentation.isRedefineClassesSupported() + "]");
		App.logMessage("VM Support for Retransform Classes Supported:[" + instrumentation.isRetransformClassesSupported() + "]");
		
		if(!instrumentation.isRedefineClassesSupported()) {
			App.logMessage("As redefine is not supported, once loaded classes cannot be redefined.");
		}

		if(!instrumentation.isRetransformClassesSupported()) {
			App.logMessage("As retransform is not supported, once loaded classes cannot be modified.");
		}

		if (agentArgs != null) {
			String[] args = agentArgs.split(",");
			for (String argument : args) {
				if (argument.startsWith(CLASSFILE_ARGUMENT)) {
					String classfile = argument.substring(CLASSFILE_ARGUMENT.length());
					loadClassesToMonitor(classfile);
				}
			}
		}
		
		jamlClassFileTransformer = TransformerFactory.getFactory().getTransformer(instrumentation);
		registerOnEventBus(jamlClassFileTransformer);
		instrumentation.addTransformer(jamlClassFileTransformer, true);
		
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		final ObjectName jamlRegistryObjectName = new ObjectName("com.ts.jaml.jmx:type=JamlRegistry");
		mBeanServer.registerMBean(jamlRegistry, jamlRegistryObjectName);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					App.logMessage("Unregistering JamlRegistry MBean.");
					mBeanServer.unregisterMBean(jamlRegistryObjectName);
				} catch (Exception e) {
					App.logMessage("Failed to unregister JamlRegistry JMX bean:" + jamlRegistryObjectName);
				}
			}
		});
	}
	
	public static void registerOnEventBus(Object instance) {
		eventBus.register(instance);
	}
	
	public static void publishEvent(JamlEvent event) {
		eventBus.post(event);
	}

	/**
	 * Loads the classes to be monitored from the specified file
	 * @param classfile path to the file
	 * @throws IOException thrown in case of failure to open the file
	 */
	private static void loadClassesToMonitor(String classfile) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(classfile)))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				ClassMonitorInfo info = Utils.getClassMonitorInfoFromString(line);
				if (info != null) {
					jamlRegistry.addClassToMonitor(info);
				}
			}
		}
	}

	public static JamlClassFileTransformer getJamlClassFileTransformer() {
		return jamlClassFileTransformer;
	}

	public static String getAgentArgs() {
		if (!initialized) {
			throw new RuntimeException("JAML not enabled properly.");
		}
		return agentArgs;
	}

	public static Instrumentation getInstrumentation() {
		if (!initialized) {
			throw new RuntimeException("JAML not enabled properly.");
		}
		return instrumentation;
	}
	
	public static void logMessage(String log) {
		System.out.println("[JAML-DEBUG] " + log);
	}
	
}
