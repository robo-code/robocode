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
package net.sf.robocode.host;


import net.sf.robocode.host.security.RobotClassLoader;
import net.sf.robocode.host.proxies.*;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.repository.RobotType;
import static net.sf.robocode.io.Logger.logError;
import net.sf.robocode.io.Logger;
import net.sf.robocode.peer.IRobotPeer;
import net.sf.robocode.security.HiddenAccess;
import robocode.Droid;
import robocode.Robot;
import robocode.control.RobotSpecification;
import robocode.robotinterfaces.*;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.*;
import java.security.AccessControlException;
import java.lang.reflect.Method;


/**
 * @author Pavel Savara (original)
 */
public class JavaHost implements IHost {
	public IRobotClassLoader createLoader(IRobotRepositoryItem robotRepositoryItem) {
		return new RobotClassLoader(robotRepositoryItem.getRobotClassPath(), robotRepositoryItem.getFullClassName());
	}

	public IHostingRobotProxy createRobotProxy(IHostManager hostManager, RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer) {
		IHostingRobotProxy robotProxy;
		final IRobotRepositoryItem specification = (IRobotRepositoryItem) HiddenAccess.getFileSpecification(
				robotSpecification);

		if (specification.isTeamRobot()) {
			robotProxy = new TeamRobotProxy(specification, hostManager, peer, statics);
		} else if (specification.isAdvancedRobot()) {
			robotProxy = new AdvancedRobotProxy(specification, hostManager, peer, statics);
		} else if (specification.isStandardRobot()) {
			robotProxy = new StandardRobotProxy(specification, hostManager, peer, statics);
		} else if (specification.isJuniorRobot()) {
			robotProxy = new JuniorRobotProxy(specification, hostManager, peer, statics);
		} else {
			throw new AccessControlException("Unknown robot type");
		}
		return robotProxy;
	}

	public String[] getReferencedClasses(IRobotRepositoryItem robotRepositoryItem) {
		IRobotClassLoader loader = null;

		try {
			loader = createLoader(robotRepositoryItem);
			loader.loadRobotMainClass(true);
			return loader.getReferencedClasses();

		} catch (ClassNotFoundException e) {
			Logger.logError(e);
			return new String[0];
		} finally {
			if (loader != null) {
				loader.cleanup();
			}
		}
	}

	public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, boolean resolve, boolean message) {
		IRobotClassLoader loader = null;

		try {
			loader = createLoader(robotRepositoryItem);
			Class<?> robotClass = loader.loadRobotMainClass(resolve);

			if (robotClass == null || java.lang.reflect.Modifier.isAbstract(robotClass.getModifiers())) {
				// this class is not robot
				return RobotType.INVALID;
			}
			return checkInterfaces(robotClass, robotRepositoryItem);

		} catch (Throwable t) {
			if (message) {
				logError("Got an error with " + robotRepositoryItem.getFullClassName() + ": " + t); // just message here
				if (t.getMessage() != null && t.getMessage().contains("Bad version number in .class file")) {
					logError("Maybe you run robocode with Java 1.5 and robot was compiled for later Java version ?");
				}
			}
			return RobotType.INVALID;
		} finally {
			if (loader != null) {
				loader.cleanup();
			}
		}
	}

	private RobotType checkInterfaces(Class<?> robotClass, IRobotRepositoryItem robotRepositoryItem) {
		boolean isJuniorRobot = false;
		boolean isStandardRobot = false;
		boolean isInteractiveRobot = false;
		boolean isPaintRobot = false;
		boolean isAdvancedRobot = false;
		boolean isTeamRobot = false;
		boolean isDroid = false;

		if (Droid.class.isAssignableFrom(robotClass)) {
			isDroid = true;
		}

		if (ITeamRobot.class.isAssignableFrom(robotClass)) {
			isTeamRobot = true;
		}

		if (IAdvancedRobot.class.isAssignableFrom(robotClass)) {
			isAdvancedRobot = true;
		}

		if (IInteractiveRobot.class.isAssignableFrom(robotClass)) {
			// in this case we make sure that robot don't waste time
			if (checkMethodOverride(robotClass, Robot.class, "getInteractiveEventListener")
					|| checkMethodOverride(robotClass, Robot.class, "onKeyPressed", KeyEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onKeyReleased", KeyEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onKeyTyped", KeyEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseClicked", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseEntered", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseExited", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMousePressed", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseReleased", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseMoved", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseDragged", MouseEvent.class)
					|| checkMethodOverride(robotClass, Robot.class, "onMouseWheelMoved", MouseWheelEvent.class)
					) {
				isInteractiveRobot = true;
			}
		}

		if (IPaintRobot.class.isAssignableFrom(robotClass)) {
			if (checkMethodOverride(robotClass, Robot.class, "getPaintEventListener")
					|| checkMethodOverride(robotClass, Robot.class, "onPaint", Graphics2D.class)
					) {
				isPaintRobot = true;
			}
		}

		if (Robot.class.isAssignableFrom(robotClass) && !isAdvancedRobot) {
			isStandardRobot = true;
		}

		if (IJuniorRobot.class.isAssignableFrom(robotClass)) {
			isJuniorRobot = true;
			if (isAdvancedRobot) {
				throw new AccessControlException(
						robotRepositoryItem.getFullClassName()
								+ ": Junior robot should not implement IAdvancedRobot interface.");
			}
		}

		if (IBasicRobot.class.isAssignableFrom(robotClass)) {
			if (!(isAdvancedRobot || isJuniorRobot)) {
				isStandardRobot = true;
			}
		}
		return new RobotType(isJuniorRobot, isStandardRobot, isInteractiveRobot, isPaintRobot, isAdvancedRobot,
				isTeamRobot, isDroid);
	}

	private boolean checkMethodOverride(Class<?> robotClass, Class<?> knownBase, String name, Class<?>... parameterTypes) {
		if (knownBase.isAssignableFrom(robotClass)) {
			final Method getInteractiveEventListener;

			try {
				getInteractiveEventListener = robotClass.getMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				return false;
			}
			if (getInteractiveEventListener.getDeclaringClass().equals(knownBase)) {
				return false;
			}
		}
		return true;
	}
	
}
