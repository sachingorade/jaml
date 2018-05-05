/*
 * Name: TestJamlRegistry.java
 *
 * Created by saching on May 5, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml.jmx;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ts.jaml.pojo.ClassMonitorInfo;
import com.ts.jaml.pojo.ExecutionTimeMonitorInfo;
import com.ts.jaml.pojo.MethodMonitorInfo;

import junit.framework.Assert;

/**
 * @author saching
 *
 */
public class TestJamlRegistry {

	private JamlRegistry jamlRegistry;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		jamlRegistry = new JamlRegistry();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassesToMonitor(java.lang.String[])}.
	 */
	@Test
	public final void testAddClassesToMonitorStringArray() {
		String class1 = "com.jaml.Test1";
		String class2 = "com.jaml.Test2";
		jamlRegistry.addClassesToMonitor(class1, class2);
		
		Assert.assertEquals(2, jamlRegistry.getClassesToBeMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class1));
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class2));
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassToMonitor(com.ts.jaml.pojo.ClassMonitorInfo)}.
	 */
	@Test
	public final void testAddClassToMonitorClassMonitorInfo() {
		String class1 = "com.jaml.Test1";
		ClassMonitorInfo classMonitorInfo = new ClassMonitorInfo(class1);
		
		jamlRegistry.addClassToMonitor(classMonitorInfo);

		Assert.assertEquals(1, jamlRegistry.getClassesToBeMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class1));
	}

	@Test
	public final void testAddClassToMonitorNullClassMonitorInfo() {
		ClassMonitorInfo classMonitorInfo = null;
		jamlRegistry.addClassToMonitor(classMonitorInfo);
		Assert.assertEquals(0, jamlRegistry.getClassesToBeMonitored().size());
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassesToMonitor(java.util.Map)}.
	 */
	@Test
	public final void testAddClassesToMonitorMapOfStringMapOfStringMethodMonitorInfo() {
		String class1 = "com.jaml.Test1";
		
		Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
		map.put(class1, null);
		
		jamlRegistry.addClassesToMonitor(map);
		
		Assert.assertEquals(1, jamlRegistry.getClassesToBeMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class1));
	}

	@Test
	public final void testAddClassesToMonitorMapOfStringMapOfStringListOfMethodMonitorInfo() {
		String class1 = "com.jaml.Test1";
		String method1 = "method1";
		
		Map<String, MethodMonitorInfo> methodMap = new HashMap<>();
		methodMap.put(method1, new ExecutionTimeMonitorInfo(method1));
		
		Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
		map.put(class1, methodMap);
		
		jamlRegistry.addClassesToMonitor(map);
		
		Assert.assertEquals(1, jamlRegistry.getClassesToBeMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class1));

		Assert.assertEquals(1, jamlRegistry.getClassesToMonitor().values().iterator().next().getMethodsToMonitor().size());
		Assert.assertEquals(method1, jamlRegistry.getClassesToMonitor().values().iterator().next().getMethodsToMonitor().keySet().iterator().next());
	}

	@Test
	public final void testAddClassesToMonitorMapOfStringMapOfStringRepeatMethodMonitorInfo() {
		String class1 = "com.jaml.Test1";
		String method1 = "method1";
		String method2 = "method2";
		
		Map<String, MethodMonitorInfo> methodMap = new HashMap<>();
		methodMap.put(method1, new ExecutionTimeMonitorInfo(method1));
		
		Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
		map.put(class1, methodMap);
		
		jamlRegistry.addClassesToMonitor(map);
		
		methodMap = new HashMap<>();
		methodMap.put(method2, new ExecutionTimeMonitorInfo(method2));
		
		map = new HashMap<>();
		map.put(class1, methodMap);
		
		jamlRegistry.addClassesToMonitor(map);
		
		Assert.assertEquals(1, jamlRegistry.getClassesToBeMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class1));
		
		Assert.assertEquals(2, jamlRegistry.getClassesToMonitor().values().iterator().next().getMethodsToMonitor().size());
		Assert.assertTrue(method1, jamlRegistry.getClassesToMonitor().values().iterator().next().getMethodsToMonitor().keySet().contains(method1));
		Assert.assertTrue(method1, jamlRegistry.getClassesToMonitor().values().iterator().next().getMethodsToMonitor().keySet().contains(method2));
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassBeingMonitored(java.lang.String[])}.
	 */
	@Test
	public final void testAddClassBeingMonitoredStringArray() {
		String class1 = "com.jaml.Test1";
		
		Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
		map.put(class1, null);
		
		jamlRegistry.addClassesToMonitor(map);
		
		jamlRegistry.addClassBeingMonitored(class1);
		
		Assert.assertEquals(1, jamlRegistry.getClassesToBeMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesToBeMonitored().contains(class1));

		Assert.assertEquals(1, jamlRegistry.getClassesBeingMonitored().size());
	}

	@Test(expected=IllegalArgumentException.class)
	public final void testAddClassBeingMonitoredInvalidClass() {
		jamlRegistry.addClassBeingMonitored("class1");
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassBeingMonitored(java.util.Map)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public final void testAddClassBeingMonitoredMapOfStringInvalidMapOfStringMethodMonitorInfo() {
		Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
		map.put("class1", null);
		jamlRegistry.addClassBeingMonitored(map);
	}

	@Test
	public final void testAddClassBeingMonitoredMapOfStringMapOfStringMethodMonitorInfo() {
		String class1 = "com.jaml.Test1";
		String method1 = "method1";
		
		Map<String, MethodMonitorInfo> methodMap = new HashMap<>();
		methodMap.put(method1, new ExecutionTimeMonitorInfo(method1));
		
		Map<String, Map<String, MethodMonitorInfo>> map = new HashMap<>();
		map.put(class1, methodMap);
		
		jamlRegistry.addClassesToMonitor(map);
		jamlRegistry.addClassBeingMonitored(map);

		Assert.assertTrue(jamlRegistry.isCurrentlyMonitored(class1));
		Assert.assertTrue(jamlRegistry.isClassMethodBeingMonitored(class1, method1));
		Assert.assertEquals(1, jamlRegistry.getClassesBeingMonitored().size());
		Assert.assertTrue(jamlRegistry.getClassesBeingMonitored().keySet().contains(class1));
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#removeClassToMonitor(java.lang.String)}.
	 */
	@Test
	public final void testRemoveClassToMonitor() {
		String className = "class1";
		jamlRegistry.addClassToMonitor(className);
		jamlRegistry.removeClassToMonitor(className);
		Assert.assertTrue(jamlRegistry.getClassesToMonitor().isEmpty());
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#removeClassesToMonitor(java.lang.String[])}.
	 */
	@Test
	public final void testRemoveClassesToMonitor() {
		String className = "class1";
		String className1 = "class2";
		jamlRegistry.addClassesToMonitor(className, className1);
		jamlRegistry.removeClassToMonitor(className);
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().isEmpty());
		jamlRegistry.removeClassToMonitor(className1);
		Assert.assertTrue(jamlRegistry.getClassesToMonitor().isEmpty());
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#removeClassesBeingMonitored(java.lang.String[])}.
	 */
	@Test
	public final void testRemoveClassesBeingMonitored() {
		String class1 = "class1";
		jamlRegistry.addClassToMonitor(class1);
		jamlRegistry.addClassBeingMonitored(class1);
		
		jamlRegistry.removeClassesBeingMonitored(class1);
		Assert.assertTrue(jamlRegistry.getClassesBeingMonitored().isEmpty());
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassToMonitor(java.lang.String)}.
	 */
	@Test
	public final void testAddClassToMonitorString() {
		String class1 = "class1";
		String method1 = "method1";
		String method2 = "method2";
		
		ClassMonitorInfo classMonitorInfo = new ClassMonitorInfo(class1);
		
		jamlRegistry.addClassToMonitor(classMonitorInfo);
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().isEmpty());
		Assert.assertNull(jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor());
		
		Map<String, MethodMonitorInfo> methodsMap = new HashMap<>();
		methodsMap.put(method1, new ExecutionTimeMonitorInfo(method1));
		
		classMonitorInfo = new ClassMonitorInfo(class1);
		classMonitorInfo.setMethods(methodsMap);
		jamlRegistry.addClassToMonitor(classMonitorInfo);
		
		Assert.assertNotNull(jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor());
		
		methodsMap = new HashMap<>();
		methodsMap.put(method2, new ExecutionTimeMonitorInfo(method2));
		
		classMonitorInfo = new ClassMonitorInfo(class1);
		classMonitorInfo.setMethods(methodsMap);
		jamlRegistry.addClassToMonitor(classMonitorInfo);
		
		Assert.assertEquals(2, jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().size());
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#addClassMethodToMonitor(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testAddClassMethodToMonitor() {
		String class1 = "class1";
		String method1 = "method1";
		jamlRegistry.addClassMethodToMonitor(class1, method1);
		
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().isEmpty());
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().isEmpty());
	}

	@Test
	public final void testAddClassMethodToMonitorRepeat() {
		String class1 = "class1";
		String method1 = "method1";
		jamlRegistry.addClassMethodToMonitor(class1, method1);
		
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().isEmpty());
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().isEmpty());

		String method2 = "method2";
		jamlRegistry.addClassMethodToMonitor(class1, method2);
		Assert.assertEquals(1, jamlRegistry.getClassesToMonitor().size());
		Assert.assertEquals(2, jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().size());

		String class2 = "class2";
		
		jamlRegistry.addClassMethodToMonitor(class2, method1);
		Assert.assertEquals(2, jamlRegistry.getClassesToMonitor().size());
		Assert.assertEquals(2, jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().size());
		Assert.assertEquals(1, jamlRegistry.getClassesToMonitor().get(class2).getMethodsToMonitor().size());
	}

	/**
	 * Test method for {@link com.ts.jaml.jmx.JamlRegistry#removeClassMethodToMonitor(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testRemoveClassMethodToMonitor() {
		String class1 = "class1";
		String method1 = "method1";
		String method2 = "method2";
		
		jamlRegistry.addClassMethodToMonitor(class1, method1);
		jamlRegistry.addClassMethodToMonitor(class1, method2);
		
		Assert.assertTrue(jamlRegistry.shouldMonitor(class1, method2));
		
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().isEmpty());
		Assert.assertTrue(jamlRegistry.shouldMonitor(class1));
		Assert.assertEquals(2, jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().size());
		
		jamlRegistry.removeClassMethodToMonitor(class1, method1);
		Assert.assertFalse(jamlRegistry.getClassesToMonitor().isEmpty());
		Assert.assertTrue(jamlRegistry.shouldMonitor(class1));
		Assert.assertEquals(1, jamlRegistry.getClassesToMonitor().get(class1).getMethodsToMonitor().size());

		jamlRegistry.removeClassMethodToMonitor(class1, method2);
		Assert.assertFalse(jamlRegistry.shouldMonitor(class1, method2));
		Assert.assertTrue(jamlRegistry.getClassesToMonitor().isEmpty());
		Assert.assertFalse(jamlRegistry.shouldMonitor(class1));
	}

}
