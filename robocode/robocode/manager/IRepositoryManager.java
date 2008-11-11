/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.manager;


import robocode.repository.Repository;

import java.io.File;
import java.util.jar.JarInputStream;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryManager {
	File getRobotCache();

	Repository getRobotRepository();

	File getRobotsDirectory();

	void clearRobotList();

	int extractJar(File f, File dest, String statusPrefix, boolean extractJars, boolean close,
			boolean alwaysReplace);

	int extractJar(JarInputStream jarIS, File dest, String statusPrefix, boolean extractJars, boolean close,
			boolean alwaysReplace); // TODO: Needs to be updated?

	boolean cleanupOldSampleRobots(boolean delete); // TODO: Needs to be updated?

	boolean verifyRootPackage(String robotName);

	RobocodeManager getManager();
}
