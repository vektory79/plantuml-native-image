# Compile the PlantUML application to Linux amd64 native image.

## Requirements

1. Installed dependency libs (for Ubuntu it is: libfreetype6 libpng16-16 libfontconfig1 libjpeg8 fonts-dejavu-extra)
2. GraalVM 19.2.0.1 (other versions can be compatible but not tested)
3. native-image compilator sould be installed (`gu install native-image`)
4. 10Gb free ram for the compilation purpose
5. Apache Maven for building the native image

## Project building

Official build of the GraalVM native-image doesn't support compilation of the AWT/Swing applications and includes 
substitution class that blocks such applications from running. Therefore, to build plantuml properly, some hacks 
must be applied.

Access to AWT API is blocked by the special class `com.oracle.svm.core.jdk.Target_java_awt_Toolkit.class` inside `svm.jar`. This JAR file can be found in two places:

* In the local Maven cache, after first compilation of the project: `~/.m2/repository/com/oracle/substratevm/svm/19.2.0.1/svm-19.2.0.1.jar`
* In the GraalVM home directory. In SDKMAN installation it is in: `~/.sdkman/candidates/java/19.2.0.1-grl/jre/lib/svm/builder/svm.jar`

Unfortunally, both of this JARs should be altered by removing `com/oracle/svm/core/jdk/Target_java_awt_Toolkit.class` file.
Otherwise execution of the program aborts with the message: `AWT is currently not supported on Substrate VM`

After patching the JARs, application can be build with the single command (current JVM must be the GraalVM JVM):
```bash
mvn
```

This will generate a native executable `plantuml` in `target` subfolder and ZIP archive with the built executable
and all required libraries included.

## Running the application

To test the application run the following command:

```bash
./target/plantuml -Djava.awt.headless=true ./examples/example1.txt
```

Currently application works in headless mode only. Desktop windows won't work due to unexpected [error](https://github.com/oracle/graal/issues/1716) in the final executable.
