/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.core;


import net.sf.robocode.io.FileUtil;
import robocode.control.events.IBattleListener;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;


/**
 * There are entry points called with reflection from HiddenAccess in robocode.api module,
 * they cross class loaders boundaries.
 * 
 * @author Pavel Savara (original)
 */
public abstract class RobocodeMainBase implements Runnable {

	private static final Logger logger = Logger.getLogger(RobocodeMainBase.class);

	public abstract void loadSetup(String[] args);

	public abstract void initForRobocodeEngine(IBattleListener listener);

	public abstract void cleanup();

	// -----------
	// entry points called with reflection from HiddenAccess in robocode.api module
	// -----------

	public static void robocodeMain(Object args) {
		// here we cross transition to EngineClassLoader classes using interface which is defined in system classLoader
		RobocodeMainBase main = Container.getComponent(RobocodeMainBase.class);

		main.loadSetup((String[]) args);

		// Make sure ALL uncaught exceptions are logged
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("UncaughtException on thread: " + t.getClass(), e);
			}
		});

		ThreadGroup group = new ThreadGroup("Robocode thread group");

		new Thread(group, main, "Robocode main thread").start();
	}

	public static void initContainer() {
		// here we cross transition to EngineClassLoader classes using interface which is defined in system classLoader
		Container.init();
	}

	public static void cleanupForRobocodeEngine() {
		// here we cross transition to EngineClassLoader classes using interface which is defined in system classLoader
		RobocodeMainBase main = Container.getComponent(RobocodeMainBase.class);

		main.cleanup();
	}

	public static void initContainerForRobocodeEngine(File robocodeHome, IBattleListener listener) {
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
			logger.error(e.getLocalizedMessage(), e);
			return;
		}

		// here we cross transition to EngineClassLoader classes using interface which is defined in system classLoader
		RobocodeMainBase main = Container.getComponent(RobocodeMainBase.class);

		main.initForRobocodeEngine(listener);
	}
}
