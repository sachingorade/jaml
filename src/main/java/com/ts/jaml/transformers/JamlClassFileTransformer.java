package com.ts.jaml.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import com.google.common.eventbus.Subscribe;
import com.ts.jaml.App;
import com.ts.jaml.events.ClassMonitorAddedEvent;
import com.ts.jaml.events.ClassMonitorRemovedEvent;
import com.ts.jaml.events.ClassMonitorUpdatedEvent;
import com.ts.jaml.jmx.JamlRegistry;

/**
 * @author saching
 *
 */
public abstract class JamlClassFileTransformer implements ClassFileTransformer {

	protected Instrumentation instrumentation;
	protected JamlRegistry jamlRegistry;
	
	public JamlClassFileTransformer(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
		jamlRegistry = JamlRegistry.getInstance();
	}
	
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] result = null;
		synchronized (jamlRegistry) {
			String classBeingLoaded = className.replace("/", ".");
			if (!jamlRegistry.shouldMonitor(classBeingLoaded)) {
				return result;
			}
			String message = null;
			if (jamlRegistry.isCurrentlyMonitored(classBeingLoaded)) {
				message = "Re-Monitoring class : " + classBeingLoaded;
			} else {
				message = "Monitoring class : " + classBeingLoaded;
			}
			App.logMessage(message);
			result = transformInternal(loader, classBeingLoaded, classBeingRedefined, protectionDomain, classfileBuffer);
		}
		return result;
	}
	
	protected abstract byte[] transformInternal(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException;

	@Subscribe
	protected void handleNewClassMonitorOperation(ClassMonitorAddedEvent event) throws ClassNotFoundException, UnmodifiableClassException {
		addClassToMonitor(event.getClassMonitorInfo().getClasssName());
	}

	@Subscribe
	protected void handleClassMonitorRemoveOperation(ClassMonitorRemovedEvent event) throws ClassNotFoundException, UnmodifiableClassException {
		removeClassFromMonitoring(event.getClassMonitorInfo().getClasssName());
	}
	
	@Subscribe
	protected void handleClassMonitorUpdatedOperation(ClassMonitorUpdatedEvent event) throws ClassNotFoundException, UnmodifiableClassException {
		// This will remove re-transform the class and update will be handled in the transformation
		removeClassFromMonitoring(event.getClassMonitorInfo().getClasssName());
	}
	
	/**
	 * This method adds a new class to be monitored by jaml
	 * @param className class to be monitored
	 * @throws UnmodifiableClassException 
	 * @throws ClassNotFoundException 
	 */
	public abstract void addClassToMonitor(String className) throws ClassNotFoundException, UnmodifiableClassException;
	
	/**
	 * This method removes the class which is being monitored right now by jaml
	 * @param className class to be removed from monitoring
	 * @throws ClassNotFoundException if the specified class is not found
	 * @throws UnmodifiableClassException if the specified class is unmodifiable class
	 */
	public abstract void removeClassFromMonitoring(String className) throws ClassNotFoundException, UnmodifiableClassException;

}
