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
 *     - Minor cleanup
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * This class is used by the system as a placeholder for all *Radians calls in
 * AdvancedRobot. You may refer to this class for documentation only.
 * <p>
 * You should create a {@link AdvancedRobot} instead.
 * <p>
 * There is no guarantee that this class will exist in future versions of Robocode.
 * <p>
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
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's body is facing, in radians.
	 */
	public double getHeadingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getHeading();
		}
		uninitializedException("getHeadingRadians");
		return 0; // never called
	}

	/**
	 * Sets the robot's body to turn left by radians when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn 180 degrees to the left
	 *   setTurnLeftRadians(Math.PI);
	 *
	 *   // Set the robot to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnLeftRadians(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnLeftRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the left
	 *    If this value is negative, the robot's body is set to turn to the right
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
	 * Sets the robot's body to turn right by radians when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn 180 degrees to the right
	 *   setTurnRightRadians(Math.PI);
	 *
	 *   // Set the robot to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnRightRadians(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnRightRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the right
	 *    If this value is negative, the robot's body is set to turn to the left
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
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the robot's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot 180 degrees to the left
	 *   turnLeftRadians(Math.PI);
	 *
	 *   // Afterwards, turn the robot 90 degrees to the right
	 *   turnLeftRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the left
	 *    If this value is negative, the robot's body is set to turn to the right
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
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot 180 degrees to the right
	 *   turnRightRadians(Math.PI);
	 *
	 *   // Afterwards, turn the robot 90 degrees to the left
	 *   turnRightRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the right
	 *    If this value is negative, the robot's body is set to turn to the left
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
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's gun is facing, in radians.
	 */
	public double getGunHeadingRadians() {
		if (peer != null) {
			return peer.getGunHeading();
		}
		uninitializedException("getGunHeadingRadians");
		return 0; // never called
	}

	/**
	 * Returns the direction that the robot's radar is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's radar is facing, in radians.
	 */
	public double getRadarHeadingRadians() {
		if (peer != null) {
			return peer.getRadarHeading();
		}
		uninitializedException("getRadarHeadingRadians");
		return 0; // never called
	}

	/**
	 * Sets the robot's gun to turn left by radians when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn 180 degrees to the left
	 *   setTurnGunLeftRadians(Math.PI);
	 *
	 *   // Set the gun to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnGunLeftRadians(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnGunLeftRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the left
	 *    If this value is negative, the robot's gun is set to turn to the right
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
	 * Sets the robot's gun to turn right by radians when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn 180 degrees to the right
	 *   setTurnGunRightRadians(Math.PI);
	 *
	 *   // Set the gun to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnGunRightRadians(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnGunRightRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the right
	 *    If this value is negative, the robot's gun is set to turn to the left
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
	 * Sets the robot's radar to turn left by radians when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn 180 degrees to the left
	 *   setTurnRadarLeftRadians(Math.PI);
	 *
	 *   // Set the radar to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnRadarLeftRadians(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnRadarLeftRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the left
	 *    If this value is negative, the robot's radar is set to turn to the right
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
	 * Sets the robot's radar to turn right by radians when the next execution
	 * takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn 180 degrees to the right
	 *   setTurnRadarRightRadians(Math.PI);
	 *
	 *   // Set the radar to turn 90 degrees to the right instead of right
	 *   // (overrides the previous order)
	 *   setTurnRadarRightRadians(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnRadarRightRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the right
	 *    If this value is negative, the robot's radar is set to turn to the left
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
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the left
	 *   turnGunLeftRadians(Math.PI);
	 *
	 *   // Afterwards, turn the robot's gun 90 degrees to the right
	 *   turnGunLeftRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the left
	 *    If this value is negative, the robot's gun is set to turn to the right
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
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the right
	 *   turnGunRightRadians(Math.PI);
	 *
	 *   // Afterwards, turn the robot's gun 90 degrees to the left
	 *   turnGunRightRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the right
	 *    If this value is negative, the robot's gun is set to turn to the left
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
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn right
	 * instead of left.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the left
	 *   turnRadarLeftRadians(Math.PI);
	 *
	 *   // Afterwards, turn the robot's radar 90 degrees to the right
	 *   turnRadarLeftRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the left
	 *    If this value is negative, the robot's radar is set to turn to the right
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
	 * <p>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn left
	 * instead of right.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the right
	 *   turnRadarRightRadians(Math.PI);
	 *
	 *   // Afterwards, turn the robot's radar 90 degrees to the left
	 *   turnRadarRightRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the right
	 *    If this value is negative, the robot's radar is set to turn to the left
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
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left.
	 *
	 * @return the angle remaining in the gun's turn, in radians
	 */
	public double getGunTurnRemainingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getGunTurnRemaining();
		}
		uninitializedException("getGunTurnRemainingRadians");
		return 0; // never called
	}

	/**
	 * Returns the angle remaining in the radar's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left.
	 *
	 * @return the angle remaining in the radar's turn, in radians
	 */
	public double getRadarTurnRemainingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getRadarTurnRemaining();
		}
		uninitializedException("getRadarTurnRemainingRadians");
		return 0; // never called
	}

	/**
	 * Returns the angle remaining in the robot's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robot's turn, in radians
	 */
	public double getTurnRemainingRadians() {
		if (peer != null) {
			peer.getCall();
			return peer.getTurnRemaining();
		}
		uninitializedException("getTurnRemainingRadians");
		return 0; // never called
	}
}