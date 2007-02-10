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
 *     - Deprecation of setColors(), where setBodyColor(), setGunColor(), and
 *       setRadarColor() must be used instead
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import robocode.peer.RobotPeer;
import robocode.exception.*;


/**
 * This class is used by the system, as well as being a placeholder for all all deprecated (meaning, you should not use them) calls.
 * <P>You should create a {@link robocode.Robot Robot} instead.
 * <P>There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see robocode.Robot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class _Robot {
	RobotPeer peer;
	private String robotImageName;
	private String gunImageName;
	private String radarImageName;

	protected _Robot() {}

	/**
	 * This method is called by the game.  RobotPeer is the object that deals with
	 * game mechanics and rules, and makes sure your robot abides by them.
	 * Do not call this method... your robot will simply stop interacting with the game.
	 */
	public final void setPeer(RobotPeer peer) {
		this.peer = peer;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/27/2001 1:36:54 PM)
	 * @param s String
	 */
	protected void uninitializedException(String s) {
		throw new RobotException(
				"You cannot call the " + s
				+ "() method before your run() method is called, or you are using a Robot object that the game doesn't know about.");
	}

	/**
	 * @deprecated use getGunHeat()
	 */
	@Deprecated
	public double getGunCharge() {
		if (peer != null) {
			return 5 - peer.getGunHeat();
		} else {
			uninitializedException("getGunCharge");
			return 0; // never called
		}
	}

	/**
	 * @deprecated Use getEnergy()
	 */
	@Deprecated
	public double getLife() {
		if (peer != null) {
			return peer.getEnergy();
		} else {
			uninitializedException("getLife");
			return 0; // never called
		}
	}

	/**
	 * @deprecated use getNumRounds() instead
	 */
	@Deprecated
	public int getNumBattles() {
		if (peer != null) {
			peer.getCall();
			return peer.getNumRounds();
		} else {
			uninitializedException("getNumBattles");
			return 0; // never called
		}
	}

	/**
	 * @deprecated use getRoundNum() instead.
	 */
	@Deprecated
	public int getBattleNum() {
		if (peer != null) {
			peer.getCall();
			return peer.getRoundNum();
		} else {
			uninitializedException("getBattleNum");
			return 0; // never called
		}
	}

	/**
	 * @deprecated This call has moved to AdvancedRobot,
	 * and will no longer function in the Robot class.
	 */
	@Deprecated
	public void setInterruptible(boolean interruptible) {}

	/**
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public String getGunImageName() {
		return gunImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public void setGunImageName(String newGunImageName) {
		gunImageName = newGunImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public void setRadarImageName(String newRadarImageName) {
		radarImageName = newRadarImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public void setRobotImageName(String newRobotImageName) {
		robotImageName = newRobotImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public String getRadarImageName() {
		return radarImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public String getRobotImageName() {
		return robotImageName;
	}
}
