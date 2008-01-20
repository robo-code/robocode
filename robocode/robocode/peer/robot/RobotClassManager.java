/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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


import java.util.*;

import robocode.io.Logger;
import robocode.manager.NameManager;
import robocode.manager.RobocodeManager;
import robocode.peer.TeamPeer;
import robocode.repository.IRobotSpecification;
import robocode.security.IRobocodeClassLoader;
import robocode.control.RobotBattleSpecification;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobotClassManager {
	private IRobotSpecification robotSpecification;
	private Class<?> robotClass;
	private Map<String, String> referencedClasses = Collections.synchronizedMap(new HashMap<String, String>());

	private IRobocodeClassLoader robotClassLoader = null;
	// only used if we're being controlled by RobocodeEngine:
	private RobotBattleSpecification controlRobotSpecification;

	private String fullClassName;
	private TeamPeer teamManager;
    private RobocodeManager manager;

    private String uid = "";

	/**
	 * RobotClassHandler constructor
	 */
	public RobotClassManager(IRobotSpecification robotSpecification, RobocodeManager manager) {
		this(robotSpecification, null, manager);
	}

	public RobotClassManager(IRobotSpecification robotSpecification, TeamPeer teamManager, RobocodeManager manager) {
		this.robotSpecification = robotSpecification;
		this.fullClassName = robotSpecification.getName();
		this.teamManager = teamManager;
        this.manager = manager;
    }

	public String getRootPackage() {
		return getClassNameManager().getRootPackage();
	}

	public NameManager getClassNameManager() {
		return robotSpecification.getNameManager();
	}

	public void addReferencedClasses(List<String> refClasses) {
		if (refClasses == null) {
			return;
		}
		for (String refClass : refClasses) {
			String className = refClass.replace('/', '.');

			if (getRootPackage() == null || !(className.startsWith("java") || className.startsWith("robocode"))) {
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
			Logger.log(fullClassName + ": Cannot set " + className + " to resolved, did not know it was referenced.");
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

	public IRobocodeClassLoader getRobotClassLoader() {
		if (robotClassLoader == null) {
            robotClassLoader = manager.createRobocodeClassLoader(this);
        }
		return robotClassLoader;
	}

	public IRobotSpecification getRobotSpecification() {
		return robotSpecification;
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
	public RobotBattleSpecification getControlRobotSpecification() {
		return controlRobotSpecification;
	}

	/**
	 * Sets the robotSpecification.
	 *
	 * @param controlRobotSpecification The robotSpecification to set
	 */
	public void setControlRobotSpecification(RobotBattleSpecification controlRobotSpecification) {
		this.controlRobotSpecification = controlRobotSpecification;
	}

	/**
	 * Gets the teamManager.
	 *
	 * @return Returns a TeamManager
	 */
	public TeamPeer getTeamManager() {
		return teamManager;
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

		robotSpecification = null;
	}
}
