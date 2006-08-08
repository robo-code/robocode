/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
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


import java.awt.Color;

import robocode.peer.RobotPeer;
import robocode.exception.*;


/**
 * This class is used by the system, as well as being a placeholder for all all deprecated (meaning, you should not use them) calls.
 * <P>You should create a {@link robocode.Robot Robot} instead.
 * <P>There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see robocode.Robot
 *
 * @author Mathew A. Nelson
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
	public void setInterruptible(boolean interruptible) {}

	/**
	 * @deprecated use setBodyColor(Color), setGunColor(Color), setRadarColor(Color) instead.
	 *
	 * Call this method to set your robot's colors.
	 * You may only call this method one time per battle.
	 * A null indicates the default (blue-ish) color.
	 * 
	 * <PRE>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 * 
	 *   public void run() {
	 *     setColors(Color.black,Color.red,new Color(150,0,150));
	 *   }
	 * </PRE>
	 * 
	 * @param bodyColor Your robot's body color
	 * @param gunColor Your robot's gun color
	 * @param radarColor Your robot's radar color
	 * @see java.awt.Color
	 */
	public void setColors(Color bodyColor, Color gunColor, Color radarColor) {
		if (peer != null) {
			peer.setCall();
			peer.setBodyColor(bodyColor);
			peer.setGunColor(gunColor);
			peer.setRadarColor(radarColor);
		} else {
			uninitializedException("setColors");
		}
	}

	/**
	 * @deprecated This call is not used.
	 */
	public String getGunImageName() {
		return gunImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	public void setGunImageName(String newGunImageName) {
		gunImageName = newGunImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	public void setRadarImageName(String newRadarImageName) {
		radarImageName = newRadarImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	public void setRobotImageName(String newRobotImageName) {
		robotImageName = newRobotImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	public String getRadarImageName() {
		return radarImageName;
	}

	/**
	 * @deprecated This call is not used.
	 */
	public String getRobotImageName() {
		return robotImageName;
	}
}
