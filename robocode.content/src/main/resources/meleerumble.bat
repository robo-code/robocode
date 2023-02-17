@rem
@rem Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v1.0
@rem which accompanies this distribution, and is available at
@rem https://robocode.sourceforge.io/license/epl-v10.html
@rem


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
java -Xmx1024M -cp "libs/*" -XX:+IgnoreUnrecognizedVMOptions "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" "--add-opens=java.desktop/sun.awt=ALL-UNNAMED" roborumble.RoboRumbleAtHome ./roborumble/meleerumble.txt
