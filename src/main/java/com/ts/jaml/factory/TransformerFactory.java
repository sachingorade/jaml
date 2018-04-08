package com.ts.jaml.factory;

import java.lang.instrument.Instrumentation;

import com.ts.jaml.transformers.JamlClassFileTransformer;
import com.ts.jaml.transformers.JavaAssistBasedClassFileTransformer;

/**
 * @author saching
 *
 */
public class TransformerFactory {

	private static TransformerFactory factory;
	
	protected TransformerFactory() {
	}
	
	public synchronized static TransformerFactory getFactory() {
		if (factory == null) {
			factory = new TransformerFactory();
		}
		return factory;
	}
	
	public JamlClassFileTransformer getTransformer(Instrumentation instrumentation) {
		return new JavaAssistBasedClassFileTransformer(instrumentation);
	}
	
}
