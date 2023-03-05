@rem
@rem Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v1.0
@rem which accompanies this distribution, and is available at
@rem https://robocode.sourceforge.io/license/epl-v10.html
@rem

@echo off

@rem ------------------------------------------------------
@rem Extract Java version
@rem ------------------------------------------------------

@rem Extract Java version
for /f "tokens=4" %%v in ('java -fullversion 2^>^&1 1^>nul') do set JAVA_VERSION=%%v

@rem Remove double quotes from Java version
set JAVA_VERSION=%JAVA_VERSION:"=%

@rem Extract Java major version
for /f "tokens=1 delims=." %%v in ("%JAVA_VERSION%") do set JAVA_MAJOR_VERSION=%%v
if %JAVA_MAJOR_VERSION% == 1 for /f "delims=. tokens=2" %%v in ("%JAVA_VERSION%") do set JAVA_MAJOR_VERSION=%%v

@rem Print out Java version being used
echo Robocode is running on Java %JAVA_MAJOR_VERSION%

@rem ------------------------------------------------------
@rem Set Java options
@rem ------------------------------------------------------

@rem Only set 'java.security.manager=allow' for Java version > 11
if %JAVA_MAJOR_VERSION% GTR 11 set _JAVA_OPTIONS="-Djava.security.manager=allow"
