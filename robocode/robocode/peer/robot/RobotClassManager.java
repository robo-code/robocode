/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added isSecurityOn()
 *     - Changed loadUnresolvedClasses() to use loadClass() instead of
 *       loadRobotClass() if security is turned off
 *     - Ported to Java 5.0
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *******************************************************************************/
package robocode.peer.robot;


import robocode.io.Logger;
import robocode.manager.NameManager;
import robocode.repository.RobotFileSpecification;
import robocode.security.RobocodeClassLoader;
import robocode.Event;
import robocode.Bullet;
import robocode.peer.RobotStatics;
import robocode.peer.BulletStatus;
import robocode.robotinterfaces.IBasicRobot;

import java.util.*;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobotClassManager {
	private RobotFileSpecification robotFileSpecification;
	private Class<?> robotClass;
	private final Map<String, String> referencedClasses = Collections.synchronizedMap(new HashMap<String, String>());

	private RobocodeClassLoader robotClassLoader = null;
	// only used if we're being controlled by RobocodeEngine:
	private robocode.control.RobotSpecification controlRobotSpecification;

	private final String fullClassName;
	private final String teamName;

	private String uid = "";

	/**
	 * RobotClassHandler constructor
	 *
	 * @param robotFileSpecification specification
	 */
	public RobotClassManager(RobotFileSpecification robotFileSpecification) {
		this(robotFileSpecification, null);
	}

	public RobotClassManager(RobotFileSpecification robotFileSpecification, String teamName) {
		this.robotFileSpecification = robotFileSpecification;
		this.fullClassName = robotFileSpecification.getName();
		this.teamName = teamName;
	}

	public String getRootPackage() {
		return getClassNameManager().getRootPackage();
	}

	public NameManager getClassNameManager() {
		return robotFileSpecification.getNameManager();
	}

	public void addReferencedClasses(List<String> refClasses) {
		if (refClasses == null) {
			return;
		}
		for (String refClass : refClasses) {
			String className = refClass.replace('/', '.');

			if (getRootPackage() == null || !(className.startsWith("java") || className.startsWith("robocode"))) { // TODO ZAMO || className.startsWith("scala")
				if (getRootPackage() == null && !className.equals(fullClassName)) {
					continue;
				}
				if (!referencedClasses.containsKey(className)) {
					referencedClasses.put(className, "false");
				}
			}
		}
	}

	public void addResolvedClass(String className) {
		if (!referencedClasses.containsKey(className)) {
			Logger.logError(
					fullClassName + ": Cannot set " + className + " to resolved, did not know it was referenced.");
			return;
		}
		referencedClasses.put(className, "true");
	}

	public String getFullClassName() {
		// Better not be null...
		return fullClassName;
	}

	public Set<String> getReferencedClasses() {
		return referencedClasses.keySet();
	}

	public Class<?> getRobotClass() {
		return robotClass;
	}

	public RobocodeClassLoader getRobotClassLoader() {
		if (robotClassLoader == null) {
			robotClassLoader = new RobocodeClassLoader(getClass().getClassLoader(), this);
		}
		return robotClassLoader;
	}

	public RobotFileSpecification getRobotSpecification() {
		return robotFileSpecification;
	}

	public void loadUnresolvedClasses() throws ClassNotFoundException {
		Iterator<String> keys = referencedClasses.keySet().iterator();

		while (keys.hasNext()) {
			String s = keys.next();

			if (referencedClasses.get(s).equals("false")) {
				// resolve, then rebuild keys...
				if (isSecutityOn()) {
					robotClassLoader.loadRobotClass(s, false);
				} else {
					robotClassLoader.loadClass(s, true);
					addResolvedClass(s);
				}
				keys = referencedClasses.keySet().iterator();
			}
		}
	}

	public void setRobotClass(Class<?> newRobotClass) {
		robotClass = newRobotClass;
	}

	@Override
	public String toString() {
		return getRobotSpecification().getNameManager().getUniqueFullClassNameWithVersion();
	}

	/**
	 * Gets the robotSpecification.
	 *
	 * @return Returns a RobotSpecification
	 */
	public robocode.control.RobotSpecification getControlRobotSpecification() {
		return controlRobotSpecification;
	}

	/**
	 * Sets the robotSpecification.
	 *
	 * @param controlRobotSpecification The robotSpecification to set
	 */
	public void setControlRobotSpecification(robocode.control.RobotSpecification controlRobotSpecification) {
		this.controlRobotSpecification = controlRobotSpecification;
	}

	/**
	 * Gets the teamManager.
	 *
	 * @return Returns a name of team
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * Gets the uid.
	 *
	 * @return Returns a long
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Sets the uid.
	 *
	 * @param uid The uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return true if the security is enabled; false otherwise
	 */
	public static boolean isSecutityOn() {
		return !System.getProperty("NOSECURITY", "false").equals("true");
	}

	public void cleanup() {
		robotClass = null;

		if (robotClassLoader != null) {
			robotClassLoader.cleanup();
			robotClassLoader = null;
		}

		if (referencedClasses != null) {
			referencedClasses.clear();
		}

		robotFileSpecification = null;
	}

	// -----------
	// helpers for accessing hidden methods on events
	// -----------

	private static IHiddenEventHelper eventHelper;
	private static IHiddenBulletHelper bulletHelper;

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

	public static void setTime(Event e, long newTime) {
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

	public static void updateBullets(Event e, Hashtable<Integer, Bullet> bullets) {
		eventHelper.updateBullets(e, bullets);
	}

	public static void update(Bullet bullet, BulletStatus status) {
		bulletHelper.update(bullet, status);
	}
}
