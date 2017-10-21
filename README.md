# Digital Custom Java Components #

This is a example project which demonstrates the creation of java implemented custom
components which can be used in the [Digital](https://github.com/hneemann/Digital/) simulator. 

## How do I get set up? ##

The easiest way to build the necessary Jar is to use [maven](https://maven.apache.org/).

* JDK 1.8 is needed (either the Oracle JDK 1.8 or OpenJDK 1.8)  
* Clone the repository.
* Replace the `Digital.jar` which is included in this repo with the version you want to use.
* Rename the DemoComponentSource class as appropriate.
* Also update the new class name in the `pom.xml`
* Implement your components
* After that run `mvn install` to create the library jar file
