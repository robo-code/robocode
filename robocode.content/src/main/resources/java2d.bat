@rem
@rem Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
@rem All rights reserved. This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License v1.0
@rem which accompanies this distribution, and is available at
@rem https://robocode.sourceforge.io/license/epl-v10.html
@rem

@echo off

@rem ------------------------------------------------------
@rem Setup Java 2D Pipeline Rendering options
@rem ------------------------------------------------------

@rem Read about settings here:
@rem Link: https://docs.oracle.com/javase/10/troubleshoot/java-2d-pipeline-rendering-and-properties.htm#JSTGD438

@rem Enable or disable Direct3D acceleration (true/false)
set J2D_D3D=true

@rem Get detailed information about the startup procedures of the Java 2D pipeline when using
@rem OpenGL or Direct3D acceleration. Remove the @rem from next line to enable the tracing:
rem set J2D_TRACE_LEVEL=4

@rem Set the Direct3D rasterizer to one of the following: ref, rgb, hal, or tnl
@rem Use ref to use the rasterizer from Microsoft
set J2D_D3D_RASTERIZER=ref
