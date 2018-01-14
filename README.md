# Digital Custom Java Components #

This is a example project which demonstrates the creation of java implemented custom
components which can be used in the [Digital](https://github.com/hneemann/Digital/) simulator. 

## How it works ##

In some cases, it makes sense not to create a special component as a subcircuit, but as a component implemented in Java.
One reason could be to improve the performance of the simulation. Another reason could be a special access to the GUI, 
which is only possible if Java code is used.

This repository shows an example containing two simple components implemented in Java.

To use these components, you must first create a jar file containing all the necessary classes.
The easiest way is to use `maven`. When the jar is created, you can attach this JAR file to the 
simulator by selecting the created JAR file - `pluginExample-.0-SNAPSHOT.jar` in this example - in 
the simulators settings ("Java library").

The class `DemoComponentSource.java` contains a `Main` method. This method can be used to debug your 
components. To use this method, it is necessary to remove the jar file from the digital settings.

## How do I get set up? ##

The easiest way to build the necessary Jar is to use [maven](https://maven.apache.org/).

* JDK 1.8 is needed (either the Oracle JDK 1.8 or OpenJDK 1.8)  
* Clone the repository.
* Replace the `Digital.jar` which is included in this repo with the version you want to use.
* Rename the DemoComponentSource class as appropriate.
* Also update the new class name in the `pom.xml`
* Implement your components
* After that run `mvn install` to create the library jar file
