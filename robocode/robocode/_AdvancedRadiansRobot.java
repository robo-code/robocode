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
 *     - Minor cleanup
 *     - Updated Javadocs
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode;


import robocode.robotinterfaces.peer.IAdvancedRobotPeer;


/**
 * This class is used by the system as a placeholder for all *Radians calls in
 * {@link AdvancedRobot}. You may refer to this class for documentation only.
 * <p/>
 * You should create a {@link AdvancedRobot} instead.
 * <p/>
 * There is no guarantee that this class will exist in future versions of Robocode.
 * <p/>
 * (The Radians methods themselves will continue work, however).
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 * @see AdvancedRobot
 */
public class _AdvancedRadiansRobot extends _AdvancedRobot {

	_AdvancedRadiansRobot() {}

	/**
	 * Returns the direction that the robot's body is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p/>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's body is facing, in radians.
	 * @see #getHeadingDegrees()
	 * @see #getGunHeadingRadians()
	 * @see #getRadarHeadingRadians()
	 */
	public double getHeadingRadians() {
		if (peer != null) {
			return peer.getBodyHeading();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Sets the robot's body to turn left by radians when the next execution
	 * takes place.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn right
	 * instead of left.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn 180 degrees to the left
	 *   setTurnLeftRadians(Math.PI);
	 * <p/>
	 *   // Set the robot to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnLeftRadians(-Math.PI / 2);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnLeftRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the left.
	 *                If {@code radians} > 0 the robot is set to turn left.
	 *                If {@code radians} < 0 the robot is set to turn right.
	 *                If {@code radians} = 0 the robot is set to stop turning.
	 * @see AdvancedRobot#setTurnLeft(double) setTurnLeft(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see AdvancedRobot#setTurnRight(double) setTurnRight(double)
	 * @see AdvancedRobot#setTurnRightRadians(double) setTurnRightRadians(double)
	 */
	public void setTurnLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(-radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the robot's body to turn right by radians when the next execution
	 * takes place.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn left
	 * instead of right.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the robot to turn 180 degrees to the right
	 *   setTurnRightRadians(Math.PI);
	 * <p/>
	 *   // Set the robot to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnRightRadians(-Math.PI / 2);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnRightRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the right.
	 *                If {@code radians} > 0 the robot is set to turn right.
	 *                If {@code radians} < 0 the robot is set to turn left.
	 *                If {@code radians} = 0 the robot is set to stop turning.
	 * @see AdvancedRobot#setTurnRight(double) setTurnRight(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see AdvancedRobot#setTurnLeft(double) setTurnLeft(double)
	 * @see AdvancedRobot#setTurnLeftRadians(double) setTurnLeftRadians(double)
	 */
	public void setTurnRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's body to the left by radians.
	 * <p/>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the robot's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn right
	 * instead of left.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot 180 degrees to the left
	 *   turnLeftRadians(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot 90 degrees to the right
	 *   turnLeftRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the left.
	 *                If {@code radians} > 0 the robot will turn right.
	 *                If {@code radians} < 0 the robot will turn left.
	 *                If {@code radians} = 0 the robot will not turn, but execute.
	 * @see #turnLeft(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarLeftRadians(double)
	 * @see #turnRadarRight(double)
	 * @see #turnRadarRightRadians(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
	 */
	public void turnLeftRadians(double radians) {
		if (peer != null) {
			peer.turnBody(-radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's body to the right by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the robot's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's body is set to turn left
	 * instead of right.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot 180 degrees to the right
	 *   turnRightRadians(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot 90 degrees to the left
	 *   turnRightRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body to the right.
	 *                If {@code radians} > 0 the robot will turn right.
	 *                If {@code radians} < 0 the robot will turn left.
	 *                If {@code radians} = 0 the robot will not turn, but execute.
	 * @see #turnRight(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarLeftRadians(double)
	 * @see #turnRadarRight(double)
	 * @see #turnRadarRightRadians(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
	 */
	public void turnRightRadians(double radians) {
		if (peer != null) {
			peer.turnBody(radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Returns the direction that the robot's gun is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p/>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's gun is facing, in radians.
	 * @see #getGunHeadingDegrees()
	 * @see #getHeadingRadians()
	 * @see #getRadarHeadingRadians()
	 */
	public double getGunHeadingRadians() {
		if (peer != null) {
			return peer.getGunHeading();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the direction that the robot's radar is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p/>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 4 means West.
	 *
	 * @return the direction that the robot's radar is facing, in radians.
	 * @see #getRadarHeadingDegrees()
	 * @see #getHeadingRadians()
	 * @see #getGunHeadingRadians()
	 */
	public double getRadarHeadingRadians() {
		if (peer != null) {
			return peer.getRadarHeading();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Sets the robot's gun to turn left by radians when the next execution
	 * takes place.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn right
	 * instead of left.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn 180 degrees to the left
	 *   setTurnGunLeftRadians(Math.PI);
	 * <p/>
	 *   // Set the gun to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnGunLeftRadians(-Math.PI / 2);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnGunLeftRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the left.
	 *                If {@code radians} > 0 the robot's gun is set to turn left.
	 *                If {@code radians} < 0 the robot's gun is set to turn right.
	 *                If {@code radians} = 0 the robot's gun is set to stop turning.
	 * @see AdvancedRobot#setTurnGunLeft(double) setTurnGunLeft(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see AdvancedRobot#setTurnGunRight(double) setTurnGunRight(double)
	 * @see AdvancedRobot#setTurnGunRightRadians(double) setTurnGunRightRadians(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
	 */
	public void setTurnGunLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnGun(-radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the robot's gun to turn right by radians when the next execution
	 * takes place.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn left
	 * instead of right.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the gun to turn 180 degrees to the right
	 *   setTurnGunRightRadians(Math.PI);
	 * <p/>
	 *   // Set the gun to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnGunRightRadians(-Math.PI / 2);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnGunRightRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the right.
	 *                If {@code radians} > 0 the robot's gun is set to turn left.
	 *                If {@code radians} < 0 the robot's gun is set to turn right.
	 *                If {@code radians} = 0 the robot's gun is set to stop turning.
	 * @see AdvancedRobot#setTurnGunRight(double) setTurnGunRight(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see AdvancedRobot#setTurnGunLeft(double) setTurnGunLeft(double)
	 * @see AdvancedRobot#setTurnGunLeftRadians(double) setTurnGunLeftRadians(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
	 */
	public void setTurnGunRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnGun(radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the robot's radar to turn left by radians when the next execution
	 * takes place.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn right
	 * instead of left.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn 180 degrees to the left
	 *   setTurnRadarLeftRadians(Math.PI);
	 * <p/>
	 *   // Set the radar to turn 90 degrees to the right instead of left
	 *   // (overrides the previous order)
	 *   setTurnRadarLeftRadians(-Math.PI / 2);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnRadarLeftRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the left.
	 *                If {@code radians} > 0 the robot's radar is set to turn left.
	 *                If {@code radians} < 0 the robot's radar is set to turn right.
	 *                If {@code radians} = 0 the robot's radar is set to stop turning.
	 * @see AdvancedRobot#setTurnRadarLeft(double) setTurnRadarLeft(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarLeftRadians(double)
	 * @see #turnRadarRight(double)
	 * @see #turnRadarRightRadians(double)
	 * @see AdvancedRobot#setTurnRadarRight(double) setTurnRadarRight(double)
	 * @see AdvancedRobot#setTurnRadarRightRadians(double) setTurnRadarRightRadians(double)
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	public void setTurnRadarLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnRadar(-radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sets the robot's radar to turn right by radians when the next execution
	 * takes place.
	 * <p/>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn left
	 * instead of right.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Set the radar to turn 180 degrees to the right
	 *   setTurnRadarRightRadians(Math.PI);
	 * <p/>
	 *   // Set the radar to turn 90 degrees to the right instead of right
	 *   // (overrides the previous order)
	 *   setTurnRadarRightRadians(-Math.PI / 2);
	 * <p/>
	 *   ...
	 *   // Executes the last setTurnRadarRightRadians()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the right.
	 *                If {@code radians} > 0 the robot's radar is set to turn left.
	 *                If {@code radians} < 0 the robot's radar is set to turn right.
	 *                If {@code radians} = 0 the robot's radar is set to stop turning.
	 * @see AdvancedRobot#setTurnRadarRight(double) setTurnRadarRight(double)
	 * @see #turnRadarRight(double)
	 * @see #turnRadarRightRadians(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarLeftRadians(double)
	 * @see AdvancedRobot#setTurnRadarLeft(double) setTurnRadarLeft(double)
	 * @see AdvancedRobot#setTurnRadarLeftRadians(double) setTurnRadarLeftRadians(double)
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	public void setTurnRadarRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnRadar(radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's gun to the left by radians.
	 * <p/>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn right
	 * instead of left.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the left
	 *   turnGunLeftRadians(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot's gun 90 degrees to the right
	 *   turnGunLeftRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the left.
	 *                If {@code radians} > 0 the robot's gun will turn left.
	 *                If {@code radians} < 0 the robot's gun will turn right.
	 *                If {@code radians} = 0 the robot's gun will not turn, but execute.
	 * @see #turnGunLeft(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarLeftRadians(double)
	 * @see #turnRadarRight(double)
	 * @see #turnRadarRightRadians(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
	 */
	public void turnGunLeftRadians(double radians) {
		if (peer != null) {
			peer.turnGun(-radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's gun to the right by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's gun is set to turn left
	 * instead of right.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the right
	 *   turnGunRightRadians(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot's gun 90 degrees to the left
	 *   turnGunRightRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun to the right.
	 *                If {@code radians} > 0 the robot's gun will turn right.
	 *                If {@code radians} < 0 the robot's gun will turn left.
	 *                If {@code radians} = 0 the robot's gun will not turn, but execute.
	 * @see #turnGunRight(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarLeftRadians(double)
	 * @see #turnRadarRight(double)
	 * @see #turnRadarRightRadians(double)
	 * @see #setAdjustGunForRobotTurn(boolean)
	 */
	public void turnGunRightRadians(double radians) {
		if (peer != null) {
			peer.turnGun(radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's radar to the left by radians.
	 * <p/>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn right
	 * instead of left.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the left
	 *   turnRadarLeftRadians(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot's radar 90 degrees to the right
	 *   turnRadarLeftRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the left.
	 *                If {@code radians} > 0 the robot's radar will turn left.
	 *                If {@code radians} < 0 the robot's radar will turn right.
	 *                If {@code radians} = 0 the robot's radar will not turn, but execute.
	 * @see #turnRadarLeft(double)
	 * @see #turnRadarRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	public void turnRadarLeftRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).turnRadar(-radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Immediately turns the robot's radar to the right by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the radar's turn is 0.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot's radar is set to turn left
	 * instead of right.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Turn the robot's radar 180 degrees to the right
	 *   turnRadarRightRadians(Math.PI);
	 * <p/>
	 *   // Afterwards, turn the robot's radar 90 degrees to the left
	 *   turnRadarRightRadians(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar to the right.
	 *                If {@code radians} > 0 the robot's radar will turn right.
	 *                If {@code radians} < 0 the robot's radar will turn left.
	 *                If {@code radians} = 0 the robot's radar will not turn, but execute.
	 * @see #turnRadarRight(double)
	 * @see #turnRadarLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnLeft(double)
	 * @see #turnLeftRadians(double)
	 * @see #turnRight(double)
	 * @see #turnRightRadians(double)
	 * @see #turnGunLeft(double)
	 * @see #turnGunLeftRadians(double)
	 * @see #turnGunRight(double)
	 * @see #turnGunRightRadians(double)
	 * @see #setAdjustRadarForRobotTurn(boolean)
	 * @see #setAdjustRadarForGunTurn(boolean)
	 */
	public void turnRadarRightRadians(double radians) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).turnRadar(radians);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Returns the angle remaining in the gun's turn, in radians.
	 * <p/>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left.
	 *
	 * @return the angle remaining in the gun's turn, in radians
	 * @see AdvancedRobot#getGunTurnRemaining()
	 * @see AdvancedRobot#getTurnRemaining() getTurnRemaining()
	 * @see #getTurnRemainingRadians()
	 * @see AdvancedRobot#getRadarTurnRemaining() getRadarTurnRemaining()
	 * @see #getRadarTurnRemainingRadians()
	 */
	public double getGunTurnRemainingRadians() {
		if (peer != null) {
			return peer.getGunTurnRemaining();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the angle remaining in the radar's turn, in radians.
	 * <p/>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left.
	 *
	 * @return the angle remaining in the radar's turn, in radians
	 * @see AdvancedRobot#getRadarTurnRemaining()
	 * @see AdvancedRobot#getTurnRemaining() getTurnRemaining()
	 * @see #getTurnRemainingRadians()
	 * @see AdvancedRobot#getGunTurnRemaining() getGunTurnRemaining()
	 * @see #getGunTurnRemainingRadians()
	 */
	public double getRadarTurnRemainingRadians() {
		if (peer != null) {
			return peer.getRadarTurnRemaining();
		}
		uninitializedException();
		return 0; // never called
	}

	/**
	 * Returns the angle remaining in the robot's turn, in radians.
	 * <p/>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robot's turn, in radians
	 * @see AdvancedRobot#getTurnRemaining()
	 * @see AdvancedRobot#getGunTurnRemaining() getGunTurnRemaining()
	 * @see #getGunTurnRemainingRadians()
	 * @see AdvancedRobot#getRadarTurnRemaining() getRadarTurnRemaining()
	 * @see #getRadarTurnRemainingRadians()
	 */
	public double getTurnRemainingRadians() {
		if (peer != null) {
			return peer.getBodyTurnRemaining();
		}
		uninitializedException();
		return 0; // never called
	}
}
