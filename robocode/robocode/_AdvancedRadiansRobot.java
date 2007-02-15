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
 *******************************************************************************/
package robocode;


/**
 * This class is used by the system as a placeholder for all *Radians calls in
 * {@link AdvancedRobot}. You may refer to this class for documentation only.
 * <P>
 * You should create a {@link AdvancedRobot} instead.
 * <P>
 * There is no guarantee that this class will exist in future versions of Robocode.
 * <P>
 * (The Radians methods themselves will continue work, however).
 *
 * @see AdvancedRobot
 *
 * @author Mathew A. Nelson (original)
 */
public class _AdvancedRadiansRobot extends _AdvancedRobot {

	protected _AdvancedRadiansRobot() {}

	/**
	 * Returns the direction that the robot's body is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's body is facing, in radians.
	 */
	public double getHeadingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getHeading();
		} else {
			uninitializedException("getHeadingRadians");
			return 0; // never called
		}
	}

	/**
	 * Sets the robot's body to turn left by radians for the next execution.
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * <P>Example:
	 * <PRE>
	 *   setTurnLeftRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's body to the left
	 */
	public void setTurnLeftRadians(double radians) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(-radians);
		} else {
			uninitializedException("setTurnLeftRadians");
		}
	}

	/**
	 * Sets the robot's to turn right by radians for the next execution.
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * <P>Example:
	 * <PRE>
	 *   setTurnRightRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's body to the right
	 */
	public void setTurnRightRadians(double radians) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnChassis(radians);
		} else {
			uninitializedException("setTurnRightRadians");
		}
	}

	/**
	 * Immediately turns the robot's body to the left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the robot's turn is 0.
	 *
	 * <P>Example:
	 * <PRE>
	 *   turnLeftRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's body to the left
	 */
	public void turnLeftRadians(double radians) {
		if (peer != null) {
			peer.turnChassis(-radians);
		} else {
			uninitializedException("turnLeftRadians");
		}
	}

	/**
	 * Immediately turns the robot's body to the right by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 *
	 * <P>Example:
	 * <PRE>
	 *   turnRightRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's body to the right
	 */
	public void turnRightRadians(double radians) {
		if (peer != null) {
			peer.turnChassis(radians);
		} else {
			uninitializedException("turnRightRadians");
		}
	}

	/**
	 * Returns the direction that the robot's gun is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's gun is facing, in radians.
	 */
	public double getGunHeadingRadians() {
		if (peer != null) {
			return peer.getGunHeading();
		} else {
			uninitializedException("getGunHeadingRadians");
			return 0; // never called
		}
	}

	/**
	 * Returns the direction that the robot's radar is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's radar is facing, in radians.
	 */
	public double getRadarHeadingRadians() {
		if (peer != null) {
			return peer.getRadarHeading();
		} else {
			uninitializedException("getRadarHeadingRadians");
			return 0; // never called
		}
	}

	/**
	 * Sets the robot's gun to turn left by radians for the next execution.
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * <P>Example:
	 * <PRE>
	 *   setTurnGunLeftRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the left
	 *
	 * @see Robot#setAdjustGunForRobotTurn
	 */
	public void setTurnGunLeftRadians(double radians) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(-radians);
		} else {
			uninitializedException("setTurnGunLeftRadians");
		}
	}

	/**
	 * Sets the robot's gun to turn right by radians for the next execution.
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * <P>Example:
	 * <PRE>
	 *   setTurnGunRightRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the right
	 *
	 * @see Robot#setAdjustGunForRobotTurn
	 */
	public void setTurnGunRightRadians(double radians) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnGun(radians);
		} else {
			uninitializedException("setTurnGunRightRadians");
		}
	}

	/**
	 * Sets the robot's radar to turn left by radians for the next execution.
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * <P>Example:
	 * <PRE>
	 *   setTurnRadarLeftRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the left
	 *
	 * @see Robot#setAdjustRadarForRobotTurn
	 * @see Robot#setAdjustRadarForGunTurn
	 */
	public void setTurnRadarLeftRadians(double radians) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnRadar(-radians);
		} else {
			uninitializedException("setTurnRadarLeftRadians");
		}
	}

	/**
	 * Sets the robot's radar to turn right by radians for the next execution.
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 *
	 * <P>Example:
	 * <PRE>
	 *   setTurnRadarRightRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the right
	 *
	 * @see Robot#setAdjustRadarForRobotTurn
	 * @see Robot#setAdjustRadarForGunTurn
	 */
	public void setTurnRadarRightRadians(double radians) {
		if (peer != null) {
			peer.setCall();
			peer.setTurnRadar(radians);
		} else {
			uninitializedException("setTurnRadarRightRadians");
		}
	}

	/**
	 * Immediately turns the robot's gun to the left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 *
	 * <P>Example:
	 * <PRE>
	 *   turnGunLeftRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the left
	 *
	 * @see Robot#setAdjustGunForRobotTurn
	 */
	public void turnGunLeftRadians(double radians) {
		if (peer != null) {
			peer.turnGun(-radians);
		} else {
			uninitializedException("turnGunLeftRadians");
		}
	}

	/**
	 * Immediately turns the robot's gun to the right by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 *
	 * <P>Example:
	 * <PRE>
	 *   turnGunRightRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the right
	 *
	 * @see Robot#setAdjustGunForRobotTurn
	 */
	public void turnGunRightRadians(double radians) {
		if (peer != null) {
			peer.turnGun(radians);
		} else {
			uninitializedException("turnGunRightRadians()");
		}
	}

	/**
	 * Immediately turns the robot's radar to the left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radars's turn is 0.
	 *
	 * <P>Example:
	 * <PRE>
	 *   turnRadarLeftRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the left
	 *
	 * @see Robot#setAdjustRadarForRobotTurn
	 * @see Robot#setAdjustRadarForGunTurn
	 */
	public void turnRadarLeftRadians(double radians) {
		if (peer != null) {
			peer.turnRadar(-radians);
		} else {
			uninitializedException("turnRadarLeftRadians");
		}
	}

	/**
	 * Immediately turns the robot's radar to the right by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 *
	 * <P>Example:
	 * <PRE>
	 *   turnRadarRightRadians(Math.PI / 2);
	 * </PRE>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the right
	 *
	 * @see Robot#setAdjustRadarForRobotTurn
	 * @see Robot#setAdjustRadarForGunTurn
	 */
	public void turnRadarRightRadians(double radians) {
		if (peer != null) {
			peer.turnRadar(radians);
		} else {
			uninitializedException("turnRadarRightRadians");
		}
	}

	/**
	 * Returns the angle remaining in the gun's turn, in radians.
	 *
	 * @return the angle remaining in the gun's turn, in radians
	 */
	public double getGunTurnRemainingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getGunTurnRemaining();
		} else {
			uninitializedException("getGunTurnRemainingRadians");
			return 0; // never called
		}
	}

	/**
	 * Returns the angle remaining in the radar's turn, in radians.
	 *
	 * @return the angle remaining in the radar's turn, in radians
	 */
	public double getRadarTurnRemainingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getRadarTurnRemaining();
		} else {
			uninitializedException("getRadarTurnRemainingRadians");
			return 0; // never called
		}
	}

	/**
	 * Returns the angle remaining in the robot's turn, in radians.
	 *
	 * @return the angle remaining in the robot's turn, in radians
	 */
	public double getTurnRemainingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getTurnRemaining();
		} else {
			uninitializedException("getTurnRemainingRadians");
			return 0; // never called
		}
	}
}