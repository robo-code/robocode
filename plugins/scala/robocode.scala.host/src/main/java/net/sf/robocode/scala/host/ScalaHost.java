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
package net.sf.robocode.scala.host;


import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.host.JavaHost;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.scala.host.security.ScalaRobotClassLoader;


/**
 * @author Pavel Savara (original)
 */
public class ScalaHost extends JavaHost {
	public IRobotClassLoader createLoader(IRobotRepositoryItem robotRepositoryItem) {
		return new ScalaRobotClassLoader(robotRepositoryItem.getRobotClassPath(), robotRepositoryItem.getFullClassName());
	}
}
