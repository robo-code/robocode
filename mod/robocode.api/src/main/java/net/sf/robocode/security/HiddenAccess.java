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


import net.sf.robocode.io.Logger;
import net.sf.robocode.manager.IRobocodeManagerBase;
import net.sf.robocode.peer.IRobotStatics;
import robocode.BattleRules;
import robocode.Bullet;
import robocode.Event;
import robocode.RobotStatus;
import robocode.control.RobotSpecification;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;


/**
 * Helpers for accessing hidden methods on events
 * @author Pavel Savara (original)
 */
public class HiddenAccess {
	public static IThreadManagerBase threadManager;
	public static IRobocodeManagerBase manager;

	private static IHiddenEventHelper eventHelper;
	private static IHiddenBulletHelper bulletHelper;
	private static IHiddenSpecificationHelper specificationHelper;
	private static IHiddenStatusHelper statusHelper;
	private static IHiddenRulesHelper rulesHelper;
	private static Method robocodeManagerFactoryRE;
	private static Method robocodeManagerFactory;
	private static boolean initialized;

	static {
		init();
	}

	private static void init() {
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

			Class<?> robocodeManager = ClassLoader.getSystemClassLoader().loadClass("robocode.manager.RobocodeManager");

			robocodeManagerFactoryRE = robocodeManager.getDeclaredMethod("createRobocodeManagerForRobotEngine",
					File.class);
			robocodeManagerFactoryRE.setAccessible(true);

			robocodeManagerFactory = robocodeManager.getDeclaredMethod("createRobocodeManager");
			robocodeManagerFactory.setAccessible(true);

			initialized = true;
		} catch (NoSuchMethodException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (ClassNotFoundException e) {
			Logger.logError(e);
		}
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

	public static IRobocodeManagerBase createRobocodeManager() {
		init();
		try {
			return (IRobocodeManagerBase) robocodeManagerFactory.invoke(null);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		}
		return null;
	}

	public static IRobocodeManagerBase createRobocodeManagerForRobotEngine(File robocodeHome) {
		init();
		try {
			return (IRobocodeManagerBase) robocodeManagerFactoryRE.invoke(null, robocodeHome);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		}
		return null;
	}
}
