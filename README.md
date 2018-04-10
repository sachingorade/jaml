# JAML [![Build Status](https://travis-ci.org/sachingorade/jaml.svg?branch=master)](https://travis-ci.org/sachingorade/jaml)
Java Application Monitoring Library (JAML). JAML was designed to be developer and tester friendly library. Current tools and libraries requires either end users to buy the license or write custom code blocks to monitor the application.

Focus of the JAML is to **minimum changes and maximum output**.

## How to use
Currently there are 2 ways JAML can be used,
- Input file
- JMX client (jconsole)

For both of these options, JAML agent must be configured in [Java application startup](https://docs.oracle.com/javase/1.5.0/docs/api/java/lang/instrument/package-summary.html).

### Input file
This method uses the Java agent arugment string to accept the name of the file which should be used as source for monitoring the classes. **classfile** is the name of the argument which should be specified to use this option.

For example, following agent string specifies that JAML library should be loaded from path /opt/jaml/jaml.jar and classes.txt file at /opt/jaml location should be used as the source for the classes to be monitored.
> -javaagent:/opt/jaml/jaml.jar=classfile:/opt/jaml/classes.txt

#### Input file format
Input file should contain the list of classes with comma separated list of methods. 

E.g.
Following line will monitor only method1ToMonitor,method2ToMonitor methods
```
com.app.package.ClassName,method1ToMonitor,method2ToMonitor
```
Following line will monitor all the methods from this class
```
com.app.package.OtherClass
```

### JMX client (jconsole)
This method allows a JMX client like jConsole to configure the classes to be monitored by the JAML library. As of now this method is more powerful as using this method you can control the classes and methods to be monitored at runtime.

**Note that** For this method to work JMX must be enabled on the server/target JVM which is to be monitored. For more info about how to enable JMX, [check this article](https://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html#gcyjz).

## Output
Once you have configured JAML in your application JAML library will start printing the monitoring logs on standard output stream with [JAML] prefix like an example below,

```
[JAML] Monitoring class : com.app.package.ClassName
[JAML] Transforming class:com.app.package.ClassName
[JAML] [Execution:com.app.package.ClassName:methodToMonitor:1523368988577:1523368988578:1:Arguments:arg1,arg2]
```
Monitoring log is printed in above format where,
[Execution:**CLASS_NAME**:**METHOD_NAME**:**START_TIME**:**END_TIME**:**TIME_EXECUTED**:Arguments:**COMMA_SEPARATE_ARGUMENT_LIST**]
