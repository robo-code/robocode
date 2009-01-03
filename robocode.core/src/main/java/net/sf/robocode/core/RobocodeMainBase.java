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
package net.sf.robocode.core;


import net.sf.robocode.security.LoggingThreadGroup;
import net.sf.robocode.manager.IRobocodeManagerBase;
import net.sf.robocode.io.FileUtil;

import java.io.File;
import java.io.IOException;


/**
 * There are entrypoints called with reflection from HiddenAccess in robocode.api module,
 * they cross classloaders boundaries.
 * 
 * @author Pavel Savara (original)
 */
public abstract class RobocodeMainBase implements Runnable {
	public abstract void loadSetup(String[] args);

	// -----------
	// entrypoints called with reflection from HiddenAccess in robocode.api module
	// -----------

	public static void robocodeMain(Object args) {
		// here we cross transition to EngineClassLoader classes using interface which is defined in system classLoader
		RobocodeMainBase main = Container.cache.getComponent(RobocodeMainBase.class);

		main.loadSetup((String[]) args);
		ThreadGroup group = new LoggingThreadGroup("Robocode thread group");

		new Thread(group, main, "Robocode main thread").start();
	}

	public static IRobocodeManagerBase createRobocodeManager() {
		// here we cross transition to EngineClassLoader classes using interface which is defined in system classLoader
		return Container.cache.getComponent(IRobocodeManagerBase.class);
	}

	public static IRobocodeManagerBase createRobocodeManagerForRobotEngine(File robocodeHome) {
		try {
			if (robocodeHome == null) {
				robocodeHome = FileUtil.getCwd();
			}
			FileUtil.setCwd(robocodeHome);

			File robotsDir = FileUtil.getRobotsDir();

			if (robotsDir == null) {
				throw new RuntimeException("No valid robot directory is specified");
			} else if (!(robotsDir.exists() && robotsDir.isDirectory())) {
				throw new RuntimeException('\'' + robotsDir.getAbsolutePath() + "' is not a valid robot directory");
			}

		} catch (IOException e) {
			System.err.println(e);
			return null;
		}

		IRobocodeManagerBase manager = createRobocodeManager();

		manager.initForRobotEngine();

		return manager;
	}

}
