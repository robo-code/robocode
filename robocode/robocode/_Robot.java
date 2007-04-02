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
 *     - Updated Javadoc
 *     - Code cleanup
 *******************************************************************************/
package robocode;


import robocode.exception.RobotException;
import robocode.peer.RobotPeer;


/**
 * This class is used by the system, as well as being a placeholder for all deprecated
 * (meaning, you should not use them) calls for Robot.
 * <p>
 * You should create a {@link Robot} instead.
 * <p>
 * There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see Robot
 *
 * @author Mathew A. Nelson (original)
 */
public class _Robot {
	RobotPeer peer;
	private String robotImageName;
	private String gunImageName;
	private String radarImageName;

	protected _Robot() {}

	/**
	 * This method is called by the game. RobotPeer is the object that deals with
	 * game mechanics and rules, and makes sure your robot abides by them.
	 * Do not call this method! Your robot will simply stop interacting with the game.
	 */
	public final void setPeer(RobotPeer peer) {
		this.peer = peer;
	}

	/**
	 * Throws a RobotException. This method should be called when the robot's peer
	 * is uninitialized.
	 *
	 * @param methodName the name of the method that failed
	 */
	protected void uninitializedException(String methodName) {
		throw new RobotException(
				"You cannot call the " + methodName
				+ "() method before your run() method is called, or you are using a Robot object that the game doesn't know about.");
	}

	/**
	 * @deprecated Use {@link Robot#getGunHeat() getGunHeat} instead.
	 */
	@Deprecated
	public double getGunCharge() {
		if (peer != null) {
			return 5 - peer.getGunHeat();
		}
		uninitializedException("getGunCharge");
		return 0; // never called
	}

	/**
	 * @deprecated Use {@link Robot#getEnergy() getEnergy} instead.
	 */
	@Deprecated
	public double getLife() {
		if (peer != null) {
			return peer.getEnergy();
		}
		uninitializedException("getLife");
		return 0; // never called
	}

	/**
	 * @deprecated Use {@link Robot#getNumRounds() getNumRounds} instead.
	 */
	@Deprecated
	public int getNumBattles() {
		if (peer != null) {
			peer.getCall();
			return peer.getNumRounds();
		}
		uninitializedException("getNumBattles");
		return 0; // never called
	}

	/**
	 * @deprecated Use {@link Robot#getRoundNum() getRoundNum} instead.
	 */
	@Deprecated
	public int getBattleNum() {
		if (peer != null) {
			peer.getCall();
			return peer.getRoundNum();
		}
		uninitializedException("getBattleNum");
		return 0; // never called
	}

	/**
	 * @deprecated This call has moved to {@link AdvancedRobot},
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
