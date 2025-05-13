@rem
@rem Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v1.0
@rem which accompanies this distribution, and is available at
@rem https://robocode.sourceforge.io/license/epl-v10.html
@rem

@echo off

@rem ------------------------------------------------------
@rem MeleeRumble
@rem ------------------------------------------------------

@rem Used for setting Java options
call set_java_options.bat

if not %ERRORLEVEL%==100 (
  @rem Run MeleeRumble
  java ^
    -cp "libs/*" ^
    -Xmx1024M ^
    -XX:+IgnoreUnrecognizedVMOptions ^
    "--add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED" ^
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" ^
    roborumble.RoboRumbleAtHome ./roborumble/meleerumble.txt
)