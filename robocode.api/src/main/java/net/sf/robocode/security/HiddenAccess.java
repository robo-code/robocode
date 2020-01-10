/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.security;


import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.io.Logger;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;


/**
 * Helpers for accessing hidden methods on events.
 *
 * @author Pavel Savara (original)
 */
public class HiddenAccess {
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
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (ClassNotFoundException e) {
			Logger.logError(e);
			if (!foundCore) {
				Logger.logError("Can't find robocode.core-1.x.jar module near to robocode.jar");
				Logger.logError("Class path: " + System.getProperty("robocode.class.path", null));
			}
			System.exit(-1);
		} catch (MalformedURLException e) {
			Logger.logError(e);
		} catch (Error e) {
			Logger.logError(e);
			throw e;
		}

	}

	private static ClassLoader getClassLoader() throws MalformedURLException {
		// if other modules are .jar next to robocode.jar on same path, we will create classloader which will load them
		// otherwise we rely on that they are already on classpath
		StringBuilder classPath = new StringBuilder(System.getProperty("java.class.path", null));
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		String path = HiddenAccess.class.getProtectionDomain().getCodeSource().getLocation().getPath();

		try {
			path = URLDecoder.decode(path, "UCS2");
		} catch (UnsupportedEncodingException e) {
			path = new File(".", "libs/robocode.jar").toString();
		}
		final int i = path.lastIndexOf("robocode.jar");

		if (i > 0) {
			loader = createClassLoader(classPath, loader, path.substring(0, i));
		}
		System.setProperty("robocode.class.path", classPath.toString());
		return loader;
	}

	private static ClassLoader createClassLoader(StringBuilder classPath, ClassLoader loader, String dir) throws MalformedURLException {

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
				final URL url = file.toURI().toURL();
				
				if (name.contains("robocode.core")) { // Robocode core
					foundCore = true;
					urls.add(url);
				}
				if (name.contains("picocontainer")) { // Picocontainer used for modularization
					urls.add(url);
				}
				if (name.contains("codesize")) { // Codesize tool
					urls.add(url);
				}
				if (name.contains("bcel")) { // BCEL used by Codesize
					urls.add(url);
				}
				if (name.contains("kotlin-stdlib")) { // Kotlin standard library
					urls.add(url);
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

	public static RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading, double radarHeading, double velocity,
			double bodyTurnRemaining, double radarTurnRemaining, double gunTurnRemaining, double distanceRemaining, double gunHeat, int others,
			int numSentries, int roundNum, int numRounds, long time) {
		return statusHelper.createStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity,
				bodyTurnRemaining, radarTurnRemaining, gunTurnRemaining, distanceRemaining, gunHeat, others, numSentries,
				roundNum, numRounds, time);
	}

	public static BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate, long inactivityTime, boolean hideEnemyNames, int sentryBorderSize) {
		return rulesHelper.createRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime,
				hideEnemyNames, sentryBorderSize);
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
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e.getCause());
			Logger.logError(e);
		}
	}

	public static void initContainer() {
		init();
		try {
			initContainer.invoke(null);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e.getCause());
			Logger.logError(e);
		}
	}

	public static void cleanup() {
		init();
		try {
			cleanup.invoke(null);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e.getCause());
			Logger.logError(e);
		}
	}

	public static void robocodeMain(final String[] args) {
		init();
		try {
			robocodeMain.invoke(null, (Object) args);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e.getCause());
			Logger.logError(e);
		}
	}

}
