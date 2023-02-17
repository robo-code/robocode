@rem
@rem Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v1.0
@rem which accompanies this distribution, and is available at
@rem https://robocode.sourceforge.io/license/epl-v10.html
@rem


@rem ------------------------------------------------------
@rem Java 2D Pipeline Rendering options
@rem ------------------------------------------------------
@rem
@rem Read about settings here:
@rem Link: https://docs.oracle.com/javase/10/troubleshoot/java-2d-pipeline-rendering-and-properties.htm#JSTGD438

@rem Enable or disable Direct3D acceleration (true/false)

set J2D_D3D=true

@rem Get detailed information about the startup procedures of the Java 2D pipeline when using
@rem OpenGL or Direct3D acceleration. Remove the @rem from next line to enable the tracing:
@rem    set J2D_TRACE_LEVEL=4

@rem Set the Direct3D rasterizer to one of the following: ref, rgb, hal, or tnl
@rem Use ref to use the rasterizer from Microsoft

set J2D_D3D_RASTERIZER=ref


@rem ------------------------------------------------------
@rem Run Robocode
@rem ------------------------------------------------------

@rem IMPORTANT: Take notice of which version of Java you are using!
@rem
@rem When running Robocode on Java 12 or never, the -Djava.security.manager=allow must be set by the
@rem user explicitly to enable the Java Security Manager, which Robocode required.
@rem
@rem Read more about this option here:
@rem https://openjdk.org/jeps/411
@rem
@rem If you are running a Java version older than Java 12, uncomment or remove the "set _JAVA_OPTIONS"
@rem line. You find the java version by writing "java -version" in a terminal.

@rem Uncomment or remove this line, if you run on a Java version older than version 12
set _JAVA_OPTIONS="-Djava.security.manager=allow"

@rem Run Robocode with Java
java -Xmx512M -cp libs/* -XX:+IgnoreUnrecognizedVMOptions "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" robocode.Robocode %*
