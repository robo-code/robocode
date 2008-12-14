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
package robocode.security;


import robocode.peer.robot.IHiddenEventHelper;
import robocode.peer.robot.IHiddenBulletHelper;
import robocode.peer.RobotStatics;
import robocode.peer.BulletStatus;
import robocode.repository.IHiddenSpecificationHelper;
import robocode.repository.FileSpecification;
import robocode.Event;
import robocode.Bullet;
import robocode.robotinterfaces.IBasicRobot;
import robocode.io.Logger;
import robocode.control.RobotSpecification;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.awt.*;
import java.util.Hashtable;


/**
 * Helpers for accessing hidden methods on events
 * @author Pavel Savara (original)
 */
public class HiddenAccess {
	private static IHiddenEventHelper eventHelper;
	private static IHiddenBulletHelper bulletHelper;
	private static IHiddenSpecificationHelper specificationHelper;

	static {
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
		} catch (NoSuchMethodException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		} catch (IllegalAccessException e) {
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

	public static void dispatch(Event event, IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
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

	public static void update(Bullet bullet, BulletStatus status) {
		bulletHelper.update(bullet, status);
	}

	public static RobotSpecification createSpecification(FileSpecification fileSpecification) {
		return specificationHelper.createSpecification(fileSpecification);
	}

	public static FileSpecification getFileSpecification(RobotSpecification specification) {
		return specificationHelper.getFileSpecification(specification);
	}

	public static String getRobotTeamName(RobotSpecification specification) {
		return specificationHelper.getTeamName(specification);
	}

	public static void setTeamName(RobotSpecification specification, String teamName) {
		specificationHelper.setTeamName(specification, teamName);
	}

	public static void setRobotValid(RobotSpecification specification, boolean value) {
		specificationHelper.setValid(specification, value);
	}
}
