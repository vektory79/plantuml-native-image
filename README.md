# Compile the PlantUML application to Linux amd64 native image.

## Copyright

Copyright (C) 2019 - 2020 vektory79.me

This code is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License version 2 only, as
published by the Free Software Foundation.  Oracle designates this
particular file as subject to the "Classpath" exception as provided
by Oracle in the LICENSE file that accompanied this code.
 
This code is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
version 2 for more details (a copy is included in the LICENSE file that
accompanied this code).
 
You should have received a copy of the GNU General Public License version
2 along with this work; if not, write to the Free Software Foundation,
Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.

## Requirements

1. Installed dependency libs (for Ubuntu it is: libfreetype6 libpng16-16 libfontconfig1 libjpeg8 fonts-dejavu-extra)
2. GraalVM 19.3.1 (other versions can be compatible but not tested)
3. native-image compilator should be installed (`gu install native-image`)
4. 10Gb free RAM for the compilation purpose
5. Apache Maven to build the native image

## Project building

Official build of the GraalVM native-image doesn't support compilation of the AWT/Swing applications and includes a 
substitution class that blocks such applications from running. Therefore, to properly build plantuml, some hacks 
must be applied.

Access to AWT API is blocked by the special class `com.oracle.svm.core.jdk.Target_java_awt_Toolkit.class` inside `svm.jar`. This JAR file can be found in two places:

* In the local Maven cache, after first compilation of the project: `~/.m2/repository/org/graalvm/nativeimage/svm/19.3.1/svm-19.3.1.jar`
* In the GraalVM home directory. In SDKMAN installation it is in: `~/.sdkman/candidates/java/19.3.1.r8-grl/jre/lib/svm/builder/svm.jar`

Unfortunally, both of this JARs have to be altered by removing `com/oracle/svm/core/jdk/Target_java_awt_Toolkit.class` file.
Otherwise execution of the program aborts with the message: `AWT is currently not supported on Substrate VM`

After patching the JARs, application can be build with the single command (current JVM must be a GraalVM JVM):
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

Currently application works in headless mode only. Desktop windows won't work due to an unexpected [error](https://github.com/oracle/graal/issues/1716) in the final executable.
