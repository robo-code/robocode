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
package net.sf.robocode.security;


import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.peer.IRobotStatics;
import robocode.BattleRules;
import robocode.Bullet;
import robocode.Event;
import robocode.RobotStatus;
import robocode.control.RobotSpecification;
import robocode.control.events.IBattleListener;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;


/**
 * Helpers for accessing hidden methods on events
 * @author Pavel Savara (original)
 */
public class HiddenAccess {
	private final static transient Logger logger = Logger.getLogger(HiddenAccess.class);

	private static IHiddenEventHelper eventHelper;
	private static IHiddenBulletHelper bulletHelper;
	private static IHiddenSpecificationHelper specificationHelper;
	private static IHiddenStatusHelper statusHelper;
	private static IHiddenRulesHelper rulesHelper;
	private static Method initContainer;
	private static Method initContainerRe;
	private static Method cleanup;
	private static Method robocodeMain;
	private static boolean initialized;
	private static boolean foundCore = false;

	public static void init() {
		if (initialized) {
			return;
		}
		Method method;

		try {
			method = Event.class.getDeclaredMethod("createHiddenHelper");
			method.setAccessible(true);
			eventHelper = (IHiddenEventHelper) method.invoke(null);
			method.setAccessible(false);

			method = Bullet.class.getDeclaredMethod("createHiddenHelper");
			method.setAccessible(true);
			bulletHelper = (IHiddenBulletHelper) method.invoke(null);
			method.setAccessible(false);

			method = RobotSpecification.class.getDeclaredMethod("createHiddenHelper");
			method.setAccessible(true);
			specificationHelper = (IHiddenSpecificationHelper) method.invoke(null);
			method.setAccessible(false);

			method = RobotStatus.class.getDeclaredMethod("createHiddenSerializer");
			method.setAccessible(true);
			statusHelper = (IHiddenStatusHelper) method.invoke(null);
			method.setAccessible(false);

			method = BattleRules.class.getDeclaredMethod("createHiddenHelper");
			method.setAccessible(true);
			rulesHelper = (IHiddenRulesHelper) method.invoke(null);
			method.setAccessible(false);

			ClassLoader loader = getClassLoader();
			Class<?> main = loader.loadClass("net.sf.robocode.core.RobocodeMainBase");

			initContainer = main.getDeclaredMethod("initContainer");
			initContainer.setAccessible(true);

			initContainerRe = main.getDeclaredMethod("initContainerForRobocodeEngine", File.class, IBattleListener.class);
			initContainerRe.setAccessible(true);

			cleanup = main.getDeclaredMethod("cleanupForRobocodeEngine");
			cleanup.setAccessible(true);

			robocodeMain = main.getDeclaredMethod("robocodeMain", Object.class);
			robocodeMain.setAccessible(true);

			initialized = true;
		} catch (NoSuchMethodException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (ClassNotFoundException e) {
			logger.error(e);
			if (!foundCore) {
				logger.fatal("Can't find robocode.core-1.x.jar module near to robocode.jar");
				logger.fatal("ClassPath: " + System.getProperty("robocode.class.path", null));
			}
			System.exit(-1);
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (Error e) {
			logger.fatal(e);
			throw e;
		}
	}

	private static ClassLoader getClassLoader() throws MalformedURLException {
		// if other modules are .jar next to robocode.jar on same path, we will create classloader which will load them
		// otherwise we rely on that they are already on classpath
		StringBuilder classPath = new StringBuilder(System.getProperty("java.class.path", null));
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		final String path = HiddenAccess.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		final int i = path.lastIndexOf("robocode.jar");

		if (i > 0) {
			loader = createClassLoader(classPath, loader, path, i);
		}
		System.setProperty("robocode.class.path", classPath.toString());
		return loader;
	}

	private static ClassLoader createClassLoader(StringBuilder classPath, ClassLoader loader, String path, int i) throws MalformedURLException {
		final String dir = path.substring(0, i);

		File dirf = new File(dir);
		ArrayList<URL> urls = new ArrayList<URL>();

		final File[] files = dirf.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				final String test = name.toLowerCase();

				return test.endsWith(".jar") && !test.endsWith("robocode.jar");
			}
		});

		if (files != null) {
			for (File file : files) {
				final String name = file.toString().toLowerCase();

				if (name.contains("robocode.core")) {
					foundCore = true;
					urls.add(file.toURI().toURL());
				}
				if (name.contains("picocontainer")) {
					urls.add(file.toURI().toURL());
				}
				classPath.append(File.pathSeparator);
				classPath.append(file.toString());
			}
		}
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), loader);
	}

	public static boolean isCriticalEvent(Event e) {
		return eventHelper.isCriticalEvent(e);
	}

	public static void setEventTime(Event e, long newTime) {
		eventHelper.setTime(e, newTime);
	}

	public static void setEventPriority(Event e, int newPriority) {
		eventHelper.setPriority(e, newPriority);
	}

	public static void dispatch(Event event, IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		eventHelper.dispatch(event, robot, statics, graphics);
	}

	public static void setDefaultPriority(Event e) {
		eventHelper.setDefaultPriority(e);
	}

	public static byte getSerializationType(Event e) {
		return eventHelper.getSerializationType(e);
	}

	public static void updateBullets(Event e, Hashtable<Integer, Bullet> bullets) {
		eventHelper.updateBullets(e, bullets);
	}

	public static void update(Bullet bullet, double x, double y, String victimName, boolean isActive) {
		bulletHelper.update(bullet, x, y, victimName, isActive);
	}

	public static RobotSpecification createSpecification(Object fileSpecification, String name, String author, String webpage, String version, String robocodeVersion, String jarFile, String fullClassName, String description) {
		return specificationHelper.createSpecification(fileSpecification, name, author, webpage, version,
				robocodeVersion, jarFile, fullClassName, description);
	}

	public static Object getFileSpecification(RobotSpecification specification) {
		return specificationHelper.getFileSpecification(specification);
	}

	public static String getRobotTeamName(RobotSpecification specification) {
		return specificationHelper.getTeamName(specification);
	}

	public static void setTeamName(RobotSpecification specification, String teamName) {
		specificationHelper.setTeamName(specification, teamName);
	}

	public static RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading, double radarHeading, double velocity, double bodyTurnRemaining, double radarTurnRemaining, double gunTurnRemaining, double distanceRemaining, double gunHeat, int others, int roundNum, int numRounds, long time) {
		return statusHelper.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
				bodyTurnRemaining, radarTurnRemaining, gunTurnRemaining, distanceRemaining, gunHeat, others, roundNum,
				numRounds, time);
	}

	public static BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate, long inactivityTime) {
		return rulesHelper.createRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime);
	}

	public static boolean isSafeThread() {
		final IThreadManagerBase threadManager = ContainerBase.getComponent(IThreadManagerBase.class);

		return threadManager != null && threadManager.isSafeThread();
	}

	public static void initContainerForRobotEngine(File robocodeHome, IBattleListener listener) {
		init();
		try {
			initContainerRe.invoke(null, robocodeHome, listener);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e.getCause());
			logger.error(e);
		}
	}

	public static void initContainer() {
		init();
		try {
			initContainer.invoke(null);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e.getCause());
			logger.error(e);
		}
	}

	public static void cleanup() {
		init();
		try {
			cleanup.invoke(null);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e.getCause());
			logger.error(e);
		}
	}

	public static void robocodeMain(final String[] args) {
		init();
		try {
			robocodeMain.invoke(null, (Object) args);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e.getCause());
			logger.error(e);
		}
	}

}
