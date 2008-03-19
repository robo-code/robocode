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
package robocode.peer.data;


import robocode.Robot;
import robocode.manager.NameManager;
import robocode.peer.TeamPeer;
import robocode.repository.RobotFileSpecification;
import robocode.robotinterfaces.IInteractiveRobot;

import java.awt.event.MouseWheelEvent;


/**
 * @author Pavel Savara (original)
 */
public class RobotPeerInfo extends RobotPeerLock {

	private boolean interactiveTested = false;
	private boolean isInteractive;
	private boolean isDuplicate;
	private boolean isJuniorRobot;
	private boolean isInteractiveRobot;
	private boolean isAdvancedRobot;
	private boolean isTeamRobot;
	private boolean isDroid;
	private boolean isIORobot;
	private boolean checkFileQuota;
	private boolean paintEnabled;
	private boolean sgPaintEnabled;
	private String name;
	private String shortName;
	private String nonVersionedName;
	private TeamPeer teamPeer;

	public void setupTeam(TeamPeer tp) {
		teamPeer = tp;
	}

	public void setupInfo2(RobotFileSpecification rfs) {
		checkReadLock();
		isJuniorRobot = rfs.isJuniorRobot();
		isInteractiveRobot = rfs.isInteractiveRobot();
		isAdvancedRobot = rfs.isAdvancedRobot();
		isTeamRobot = rfs.isTeamRobot();
		isDroid = rfs.isDroid();
	}

	public final void cleanup() {
		if (teamPeer != null) {
			teamPeer.clear();
		}
		teamPeer = null;
	}

	public TeamPeer getTeamPeer() {
		checkReadLock();
		return teamPeer;
	}

	public boolean isTeamLeader() {
		checkReadLock();
		return (teamPeer != null && teamPeer.getTeamLeader() == peer);
	}

	public boolean isPaintEnabled() {
		checkReadLock();
		return paintEnabled;
	}

	public boolean isSGPaintEnabled() {
		checkReadLock();
		return sgPaintEnabled;
	}

	public boolean isCheckFileQuota() {
		checkReadLock();
		return checkFileQuota;
	}

	public boolean isIORobot() {
		checkReadLock();
		return isIORobot;
	}

	public boolean isDuplicate() {
		checkReadLock();
		return isDuplicate;
	}

	public boolean isDroid() {
		checkReadLock();
		return isDroid;
	}

	/**
	 * Returns <code>true</code> if the robot is implementing the
	 * {@link robocode.robotinterfaces.IJuniorRobot}; <code>false</code> otherwise.
	 */
	public boolean isJuniorRobot() {
		checkReadLock();
		return isJuniorRobot;
	}

	/**
	 * Returns <code>true</code> if the robot is implementing the
	 * {@link robocode.robotinterfaces.IInteractiveRobot}; <code>false</code> otherwise.
	 */
	public boolean isInteractiveRobot() {
		checkReadLock();
		return isInteractiveRobot;
	}

	/**
	 * Returns <code>true</code> if the robot is implementing the
	 * {@link robocode.robotinterfaces.IAdvancedRobot}; <code>false</code> otherwise.
	 */
	public boolean isAdvancedRobot() {
		checkReadLock();
		return isAdvancedRobot;
	}

	/**
	 * Returns <code>true</code> if the robot is implementing the
	 * {@link robocode.robotinterfaces.ITeamRobot}; <code>false</code> otherwise.
	 */
	public boolean isTeamRobot() {
		checkReadLock();
		return isTeamRobot;
	}

	/**
	 * Robot class is forcing IInteractiveEvents into robot inheritance tree.
	 * This method is optimizing it away in case that inherited "client" robot
	 * is not overriding any of interactive methods/event handlers
	 *
	 * @return true if the robot is IInteractiveRobot not inherited from Robot or
	 *         is Robot descendant with any interactive handlers overriden
	 */
	public boolean isInteractiveListener() {
		checkReadLock();
		if (!interactiveTested) {
			interactiveTested = true;

			if (!isInteractiveRobot()) {
				isInteractive = false;
				return isInteractive;
			}
			IInteractiveRobot r = (IInteractiveRobot) peer.getRobot();

			if (r == null) {
				isInteractive = false;
				return isInteractive;
			}
			if (!Robot.class.isAssignableFrom(r.getClass())) {
				isInteractive = true;
				return isInteractive;
			}
			isInteractive = testInteractiveHandler(r, "onKeyPressed", java.awt.event.KeyEvent.class)
					|| testInteractiveHandler(r, "onKeyReleased", java.awt.event.KeyEvent.class)
					|| testInteractiveHandler(r, "onKeyTyped", java.awt.event.KeyEvent.class)
					|| testInteractiveHandler(r, "onMouseClicked", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMouseEntered", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMouseExited", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMousePressed", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMouseReleased", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMouseMoved", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMouseDragged", java.awt.event.MouseEvent.class)
					|| testInteractiveHandler(r, "onMouseWheelMoved", MouseWheelEvent.class)
					|| testInteractiveHandler(r, "onKeyTyped", java.awt.event.MouseEvent.class);
		}
		return isInteractive;
	}

	private boolean testInteractiveHandler(IInteractiveRobot r, String name, Class<?> eventClass) {
		checkReadLock();
		Class<?> c = r.getClass();

		do {
			try {
				if (c.getDeclaredMethod(name, eventClass) != null) {
					return true;
				}
			} catch (NoSuchMethodException e) {// intentionaly empty
			}
			c = c.getSuperclass();
		} while (c != Robot.class);

		return false;
	}

	public String getFullClassNameWithVersion() {
		checkReadLock();
		return peer.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion();
	}

	public String getUniqueFullClassNameWithVersion() {
		checkReadLock();
		return peer.getRobotClassManager().getClassNameManager().getUniqueFullClassNameWithVersion();
	}

	public String getName() {
		// checkReadLock();
		// intentionaly not synchronized to prevent block from user code
		return (name != null)
				? shortName
				: peer.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion();
	}

	public String getShortName() {
		checkReadLock();
		return (shortName != null)
				? shortName
				: peer.getRobotClassManager().getClassNameManager().getUniqueShortClassNameWithVersion();
	}

	public String getVeryShortName() {
		checkReadLock();
		return (shortName != null)
				? shortName
				: peer.getRobotClassManager().getClassNameManager().getUniqueVeryShortClassNameWithVersion();
	}

	public String getNonVersionedName() {
		checkReadLock();
		return (nonVersionedName != null)
				? nonVersionedName
				: peer.getRobotClassManager().getClassNameManager().getFullClassName();
	}

	public void setDuplicate(int count) {
		checkWriteLock();
		isDuplicate = true;

		String countString = " (" + (count + 1) + ')';

		NameManager cnm = peer.getRobotClassManager().getClassNameManager();

		name = cnm.getFullClassNameWithVersion() + countString;
		shortName = cnm.getUniqueShortClassNameWithVersion() + countString;
		nonVersionedName = cnm.getFullClassName() + countString;
	}

	public void setCheckFileQuota(boolean newCheckFileQuota) {
		checkWriteLock();
		peer.getOut().println("CheckFileQuota on");
		checkFileQuota = newCheckFileQuota;
	}

	public void setIORobot(boolean ioRobot) {
		checkWriteLock();
		this.isIORobot = ioRobot;
	}

	public void setPaintEnabled(boolean enabled) {
		checkWriteLock();
		paintEnabled = enabled;
	}

	public void setSGPaintEnabled(boolean enabled) {
		checkWriteLock();
		sgPaintEnabled = enabled;
	}
}
