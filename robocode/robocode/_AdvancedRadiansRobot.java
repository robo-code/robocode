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
 *******************************************************************************/
package robocode;


/**
 * This class is used by the system as a placeholder for all *Radians calls in AdvancedRobot.
 * You may refer to it for documentation.
 * <P>You should create a {@link robocode.AdvancedRobot AdvancedRobot} instead.
 * <P>There is no guarantee that this class will exist in future versions of Robocode.
 * <P>(The Radians methods themselves will continue work, however).
 *
 * @see robocode.AdvancedRobot
 *
 * @author Mathew A. Nelson
 */
public class _AdvancedRadiansRobot extends _AdvancedRobot {
	
	protected _AdvancedRadiansRobot() {}
	
	/**
	 * Returns the direction the robot is facing, in radians.  
	 *  The value returned will be between 0 and 2 * PI.
	 * @return the direction the robot is facing, in radians.
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
	 * Sets the robot to turn left by radians.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the robot to turn right by radians.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Rotates your robot.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnLeftRadians(Math.PI / 2);
	 * </PRE>
	 * @param radians How many radians to rotate left.
	 */
	public void turnLeftRadians(double radians) {
		if (peer != null) {
			peer.turnChassis(-radians);
		} else {
			uninitializedException("turnLeftRadians");
		}
	}

	/**
	 * Rotates your robot.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnRightRadians(Math.PI / 2);
	 * </PRE>
	 * @param radians How many radians to rotate right.
	 */
	public void turnRightRadians(double radians) {
		if (peer != null) {
			peer.turnChassis(radians);
		} else {
			uninitializedException("turnRightRadians");
		}
	}

	/**
	 * Returns gun heading in radians.  This is a value from 0 to 2*Pi, where 0 points to the top of the screen.
	 * @return gun heading
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
	 * Returns radar heading in radians.  This is a value from 0 to 2*Pi, where 0 points to the top of the screen.
	 * @return radar heading
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
	 * Sets the gun to turn left by radians.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the gun to turn right by radians.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the radar to turn left by radians.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Sets the radar to turn right by radians.
	 * This call returns immediately, and will not execute until you call execute() or take an action that executes.
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
	 * Rotates your robot's gun.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnGunLeftRadians(Math.PI /2);
	 * </PRE>
	 * @param radians How many radians to rotate the gun left.
	 */
	public void turnGunLeftRadians(double radians) {
		if (peer != null) {
			peer.turnGun(-radians);
		} else {
			uninitializedException("turnGunLeftRadians");
		}
	}

	/**
	 * Rotates your robot's gun.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnGunRightRadians(Math.PI / 2);
	 * </PRE>
	 * @param radians How many radians to rotate the gun right.
	 */
	public void turnGunRightRadians(double radians) {
		if (peer != null) {
			peer.turnGun(radians);
		} else {
			uninitializedException("turnGunRightRadians()");
		}
	}

	/**
	 * Rotates your robot's radar.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnRadarLeftRadians(Math.PI / 2);
	 * </PRE>
	 * @param radians How many radians to rotate the radar left.
	 */
	public void turnRadarLeftRadians(double radians) {
		if (peer != null) {
			peer.turnRadar(-radians);
		} else {
			uninitializedException("turnRadarLeftRadians");
		}
	}

	/**
	 * Rotates your robot's radar.
	 * This call executes immediately, and does not return until it is complete.
	 *
	 * <P>Example
	 * <PRE>
	 *   turnRadarRightRadians(Math.PI / 2);
	 * </PRE>
	 * @param radians How many radians to rotate the radar right.
	 */
	public void turnRadarRightRadians(double radians) {
		if (peer != null) {
			peer.turnRadar(radians);
		} else {
			uninitializedException("turnRadarRightRadians");
		}
	}

	/**
	 * Gets angle remaining in the gun's turn, in radians
	 * 
	 * @return angle remaining in the gun's turn, in radians
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
	 * Gets angle remaining in the radar's turn, in radians
	 * 
	 * @return angle remaining in the radar's turn, in radians
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
	 * Gets angle remaining in the robot's turn, in radians.
	 * 
	 * @return angle remaining in the robot's turn, in radians
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
