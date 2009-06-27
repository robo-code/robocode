/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.scala.host.security;


import net.sf.robocode.host.security.RobotClassLoader;
import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.core.Container;

import java.net.URL;
import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class ScalaRobotClassLoader extends RobotClassLoader {
	public ScalaRobotClassLoader(URL robotClassPath, String robotFullClassName) {
		super(robotClassPath, robotFullClassName);
		for (URL jar : Container.findJars(File.separator + "scala-library-")) {
			super.addURL(jar);
		}
	}
}
