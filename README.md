# JAML
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

### JMX client (jconsole)
This method allows a JMX client like jConsole to configure the classes to be monitored by the JAML library. As of now this method is more powerful as using this method you can control the classes and methods to be monitored at runtime.
