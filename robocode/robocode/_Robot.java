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
 *     - Updated Javadoc
 *     - Code cleanup
 *     - Changed to extend the new _RobotBase class instead of being top class
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore, and was moved to the new _RobotBase class
 *     - The setPeer() methods has been moved to the _RobotPeer class
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode;


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
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public abstract class _Robot extends _RobotBase {
	private String robotImageName;
	private String gunImageName;
	private String radarImageName;

	protected _Robot() {}

	/**
	 * @deprecated Use {@link Robot#getGunHeat() getGunHeat} instead.
	 */
	@Deprecated
	public double getGunCharge() {
		if (peer != null) {
			return 5 - peer.getGunHeat();
		}
		uninitializedException();
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
		uninitializedException();
		return 0; // never called
	}

	/**
	 * @deprecated Use {@link Robot#getNumRounds() getNumRounds} instead.
	 */
	@Deprecated
	public int getNumBattles() {
		if (peer != null) {
			return peer.getNumRounds();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * @deprecated Use {@link Robot#getRoundNum() getRoundNum} instead.
	 */
	@Deprecated
	public int getBattleNum() {
		if (peer != null) {
			return peer.getRoundNum();
		}
		uninitializedException();
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
