/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


/**
 * This class is used by the system, as well as being a placeholder for all deprecated
 * (meaning, you should not use them) calls for {@link Robot}.
 * <p>
 * You should create a {@link Robot} instead.
 *
 * @see Robot
 * @see JuniorRobot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see RateControlRobot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public abstract class _Robot extends _RobotBase {
	private String robotImageName;
	private String gunImageName;
	private String radarImageName;

	_Robot() {}

	/**
	 * @return 5 - {@link robocode.Robot#getGunHeat() getGunHeat()}.
	 * @deprecated Use {@link Robot#getGunHeat() getGunHeat()} instead.
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
	 * @return the robot's current life/energy.
	 * @deprecated Use {@link Robot#getEnergy() getEnergy()} instead.
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
	 * @return the number of rounds in the current battle
	 * @deprecated Use {@link Robot#getNumRounds() getNumRounds()} instead.
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
	 * @return the current round number of the battle (zero indexed).
	 * @deprecated Use {@link Robot#getRoundNum() getRoundNum()} instead.
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
	 * This call has moved to {@link AdvancedRobot}, and will no longer function in
	 * the {@link Robot} class.
	 *
	 * @param interruptible {@code true} if the event handler should be
	 *                      interrupted if new events of the same priority occurs; {@code false}
	 *                      otherwise
	 */
	public void setInterruptible(boolean interruptible) {}

	/**
	 * @return the name of the gun image
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public String getGunImageName() {
		return gunImageName;
	}

	/**
	 * @param newGunImageName the name of the new gun image
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public void setGunImageName(String newGunImageName) {
		gunImageName = newGunImageName;
	}

	/**
	 * @param newRadarImageName the name of the new radar image
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public void setRadarImageName(String newRadarImageName) {
		radarImageName = newRadarImageName;
	}

	/**
	 * @param newRobotImageName the name of the new robot body image
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public void setRobotImageName(String newRobotImageName) {
		robotImageName = newRobotImageName;
	}

	/**
	 * @return the name of the radar image
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public String getRadarImageName() {
		return radarImageName;
	}

	/**
	 * @return the name of the robot image
	 * @deprecated This call is not used.
	 */
	@Deprecated
	public String getRobotImageName() {
		return robotImageName;
	}
}
