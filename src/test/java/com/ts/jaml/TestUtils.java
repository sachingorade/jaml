/*
 * Name: TestUtils.java
 *
 * Created by saching on Apr 24, 2018
 *
 * Description: TODO saching
 *
 */
package com.ts.jaml;

import org.junit.Test;

import com.ts.jaml.pojo.ClassMonitorInfo;
import com.ts.jaml.pojo.ExecutionTimeMonitorInfo;
import com.ts.jaml.pojo.InvocationCounterMonitorInfo;
import com.ts.jaml.pojo.MethodMonitorInfo;
import com.ts.jaml.pojo.VariableValueMonitorInfo;

import junit.framework.Assert;

/**
 * @author saching
 *
 */
public class TestUtils {

	@Test
	public final void testGetMethodMonitorInfoFromStringForNoPrefix() {
		MethodMonitorInfo methodMonitorInfo = Utils.getMethodMonitorInfoFromString("myMethod");
		Assert.assertNotNull(methodMonitorInfo);
		Assert.assertTrue(methodMonitorInfo instanceof ExecutionTimeMonitorInfo);
	}
	
	@Test
	public final void testGetMethodMonitorInfoFromStringForExecPrefix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("exec:myMethod");
		Assert.assertNotNull(info);
		Assert.assertTrue(info instanceof ExecutionTimeMonitorInfo);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForExecPrefixPrintVars() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("exec:myMethod(printVars)");
		Assert.assertNotNull(info);
		Assert.assertTrue(info instanceof ExecutionTimeMonitorInfo);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForExecPrefixWith_$() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("exec:_myMethod$");
		Assert.assertNotNull(info);
		Assert.assertTrue(info instanceof ExecutionTimeMonitorInfo);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForExecPrefixWith$_() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("exec:$my_Method");
		Assert.assertNotNull(info);
		Assert.assertTrue(info instanceof ExecutionTimeMonitorInfo);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForInvalidExecPrefix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("exec::myMethod");
		Assert.assertNull(info);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForInvocPrefix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("invoc:myMethod");
		Assert.assertNotNull(info);
		Assert.assertTrue(info instanceof InvocationCounterMonitorInfo);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForInvalidInvocPrefix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("invocation:myMethod");
		Assert.assertNull(info);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForVarPrefix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("var:myMethod(myVar-10)");
		Assert.assertNotNull(info);
		Assert.assertTrue(info instanceof VariableValueMonitorInfo);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForInvalidVarPrefix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("var:var:myMethod(myVar-10)");
		Assert.assertNull(info);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForInvalidVarSuffix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("var:myMethod(myVar,10)");
		Assert.assertNull(info);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForMalformedVarSuffix() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("var:myMethod(myVar,10");
		Assert.assertNull(info);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForNull() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString(null);
		Assert.assertNull(info);
	}

	@Test
	public final void testGetMethodMonitorInfoFromStringForEmptyString() {
		MethodMonitorInfo info = Utils.getMethodMonitorInfoFromString("");
		Assert.assertNull(info);
	}

	@Test
	public final void testGetClassMonitorInfoFromStringValidPattern() {
		String className = "com.ts.jaml.Utils";
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(className);
		Assert.assertNotNull(classMonitorInfo);
		Assert.assertNull(classMonitorInfo.getMethodsToMonitor());
		Assert.assertEquals(className, classMonitorInfo.getClasssName());
	}

	@Test
	public final void testGetClassMonitorInfoFromStringHalfValidPatternNoMethods() {
		String className = "com.ts.jaml.Utils";
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(className + "=");
		Assert.assertNotNull(classMonitorInfo);
		Assert.assertNull(classMonitorInfo.getMethodsToMonitor());
		Assert.assertEquals(className, classMonitorInfo.getClasssName());
	}

	@Test
	public final void testGetClassMonitorInfoFromStringValidPatternNoMethods() {
		String className = "com.ts.jaml.Utils";
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(className + "=myMethod");
		Assert.assertNotNull(classMonitorInfo);
		Assert.assertNotNull(classMonitorInfo.getMethodsToMonitor());
		Assert.assertEquals(1, classMonitorInfo.getMethodsToMonitor().size());
		Assert.assertEquals(className, classMonitorInfo.getClasssName());
	}

	@Test
	public final void testGetClassMonitorInfoFromStringValidPatternMixedMethods() {
		String className = "com.ts.jaml.Utils";
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(className + "=myMethod,   ,");
		Assert.assertNotNull(classMonitorInfo);
		Assert.assertNotNull(classMonitorInfo.getMethodsToMonitor());
		Assert.assertEquals(1, classMonitorInfo.getMethodsToMonitor().size());
		Assert.assertEquals(className, classMonitorInfo.getClasssName());
	}

	@Test
	public final void testGetClassMonitorInfoFromStringValidPatternMixedMethodsMonitors() {
		String className = "com.ts.jaml.Utils";
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(className + "=exec:myMethod,invoc:SecondMethod");
		Assert.assertNotNull(classMonitorInfo);
		Assert.assertNotNull(classMonitorInfo.getMethodsToMonitor());
		Assert.assertEquals(2, classMonitorInfo.getMethodsToMonitor().size());
		Assert.assertEquals(className, classMonitorInfo.getClasssName());
	}

	@Test
	public final void testGetClassMonitorInfoFromStringValidPatternInvalidMethodsMonitor() {
		String className = "com.ts.jaml.Utils";
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(className + "=123Method");
		Assert.assertNotNull(classMonitorInfo);
		Assert.assertNull(classMonitorInfo.getMethodsToMonitor());
		Assert.assertEquals(className, classMonitorInfo.getClasssName());
	}

	@Test
	public final void testGetClassMonitorInfoFromStringForNull() {
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString(null);
		Assert.assertNull(classMonitorInfo);
	}

	@Test
	public final void testGetClassMonitorInfoFromStringForEmptyString() {
		ClassMonitorInfo classMonitorInfo = Utils.getClassMonitorInfoFromString("   ");
		Assert.assertNull(classMonitorInfo);
	}

}
