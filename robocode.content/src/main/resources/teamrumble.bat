@rem
@rem Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v1.0
@rem which accompanies this distribution, and is available at
@rem https://robocode.sourceforge.io/license/epl-v10.html
@rem

@echo off

@rem ------------------------------------------------------
@rem TeamRumble
@rem ------------------------------------------------------

@rem Used for setting _JAVA_OPTIONS
call set_java_options.bat

if not %ERRORLEVEL%==100 (
  @rem Run TeamRumble
  java ^
    -cp "libs/*" ^
    -Xmx512M ^
    -XX:+IgnoreUnrecognizedVMOptions ^
    "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" ^
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" ^
    roborumble.RoboRumbleAtHome ./roborumble/teamrumble.txt
)