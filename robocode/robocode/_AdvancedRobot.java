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
 *     - Added a peer.getCall() to getWaitCount()
 *     - Fixed wrong names used in uninitializedException() calls
 *     - Updated Javadocs
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode;


import robocode.robotinterfaces.peer.IAdvancedRobotPeer;


/**
 * This class is used by the system, as well as being a placeholder for all deprecated
 * (meaning, you should not use them) calls for AdvancedRobot.
 * <P>
 * You should create a {@link AdvancedRobot} instead.
 * <P>
 * There is no guarantee that this class will exist in future versions of Robocode.
 *
 * @see AdvancedRobot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public class _AdvancedRobot extends Robot {

	protected _AdvancedRobot() {}

	/**
     * @deprecated Use {@link AdvancedRobot#setTurnGunLeft(double)
	 *    setTurnGunLeft} instead.
     *
     * @param degrees the amount of degrees to turn the robot's gun to the left.
     *    If {@code degrees} > 0 the robot's gun is set to turn left.
     *    If {@code degrees} < 0 the robot's gun is set to turn right.
     *    If {@code degrees} = 0 the robot's gun is set to stop turning.
	 */
	@Deprecated
	public void setTurnGunLeftDegrees(double degrees) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnGun(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnGunRight(double)
	 *    setTurnGunRight} instead.
     *
     * @param degrees the amount of degrees to turn the robot's gun to the right.
     *    If {@code degrees} > 0 the robot's gun is set to turn right.
     *    If {@code degrees} < 0 the robot's gun is set to turn left.
     *    If {@code degrees} = 0 the robot's gun is set to stop turning.
	 */
	@Deprecated
	public void setTurnGunRightDegrees(double degrees) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnGun(Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link Robot#turnRadarRight(double) turnRadarRight}
	 *    instead.
     *
     * @param degrees the amount of degrees to turn the robot's radar to the right.
     *    If {@code degrees} > 0 the robot's radar will turn right.
     *    If {@code degrees} < 0 the robot's radar will turn left.
     *    If {@code degrees} = 0 the robot's radar will not turn, but execute.
	 */
	@Deprecated
	public void turnRadarRightDegrees(double degrees) {
		turnRadarRight(degrees);
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnRight(double)
	 *    setTurnRight(double)} instead.
     *
     * @param degrees the amount of degrees to turn the robot's body to the right.
     *    If {@code degrees} > 0 the robot is set to turn right.
     *    If {@code degrees} < 0 the robot is set to turn left.
     *    If {@code degrees} = 0 the robot is set to stop turning.
	 */
	@Deprecated
	public void setTurnRightDegrees(double degrees) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnRadarLeft(double)
	 *    setTurnRadarLeft(double)} instead.
     *
     * @param degrees the amount of degrees to turn the robot's radar to the left.
     *    If {@code degrees} > 0 the robot's radar is set to turn left.
     *    If {@code degrees} < 0 the robot's radar is set to turn right.
     *    If {@code degrees} = 0 the robot's radar is set to stop turning.
	 */
	@Deprecated
	public void setTurnRadarLeftDegrees(double degrees) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnRadar(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnLeft(double)
	 *    setTurnLeft(double)} instead.
     *
     * @param degrees the amount of degrees to turn the robot's body to the left.
     *    If {@code degrees} > 0 the robot is set to turn left.
     *    If {@code degrees} < 0 the robot is set to turn right.
     *    If {@code degrees} = 0 the robot is set to stop turning.
	 */
	@Deprecated
	public void setTurnLeftDegrees(double degrees) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnBody(-Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link Robot#getHeading() getHeading()} instead.
     *
     * @return the direction that the robot's body is facing, in degrees.
	 */
	@Deprecated
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @deprecated Use {@link Robot#turnLeft(double) turnLeft(double)} instead.
     *
     * @param degrees the amount of degrees to turn the robot's body to the left.
     *    If {@code degrees} > 0 the robot will turn left.
     *    If {@code degrees} < 0 the robot will turn right.
     *    If {@code degrees} = 0 the robot will not turn, but execute.
	 */
	@Deprecated
	public void turnLeftDegrees(double degrees) {
		turnLeft(degrees);
	}

	/**
	 * @deprecated Use {@link Robot#turnRight(double) turnRight(double)} instead.
     *
     * @param degrees the amount of degrees to turn the robot's body to the right.
     *    If {@code degrees} > 0 the robot will turn right.
     *    If {@code degrees} < 0 the robot will turn left.
     *    If {@code degrees} = 0 the robot will not turn, but execute.
	 */
	@Deprecated
	public void turnRightDegrees(double degrees) {
		turnRight(degrees);
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#execute() execute} instead.
	 */
	@Deprecated
	public void endTurn() {
		if (peer != null) {
			peer.execute();
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link Robot#getGunHeading() getGunHeading()} instead.
     *
     * @return the direction that the robot's gun is facing, in degrees.
	 */
	@Deprecated
	public double getGunHeadingDegrees() {
		return getGunHeading();
	}

	/**
	 * @deprecated Use {@link Robot#getRadarHeading() getRadarHeading()} instead.
     *
     * @return the direction that the robot's radar is facing, in degrees.
	 */
	@Deprecated
	public double getRadarHeadingDegrees() {
		return getRadarHeading();
	}

	/**
	 * @deprecated This method is no longer functional.
	 * Use {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)} instead.
     *
     * @return allways {@code 0} as this method is no longer functional.
	 */
	@Deprecated
	public int getWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}

	/**
	 * @deprecated Use {@link AdvancedRobot#setTurnRadarRight(double)
	 *    setTurnRadarRight} instead.
     *
     * @param degrees the amount of degrees to turn the robot's radar to the right.
     *    If {@code degrees} > 0 the robot's radar is set to turn right.
     *    If {@code degrees} < 0 the robot's radar is set to turn left.
     *    If {@code degrees} = 0 the robot's radar is set to stop turning.
	 */
	@Deprecated
	public void setTurnRadarRightDegrees(double degrees) {
		if (peer != null) {
			((IAdvancedRobotPeer) peer).setTurnRadar(Math.toRadians(degrees));
		} else {
			uninitializedException();
		}
	}

	/**
	 * @deprecated Use {@link Robot#turnGunLeft(double) turnGunLeft} instead.
     *
     * @param degrees the amount of degrees to turn the robot's gun to the left.
     *    If {@code degrees} > 0 the robot's gun will turn left.
     *    If {@code degrees} < 0 the robot's gun will turn right.
     *    If {@code degrees} = 0 the robot's gun will not turn, but execute.
	 */
	@Deprecated
	public void turnGunLeftDegrees(double degrees) {
		turnGunLeft(degrees);
	}

	/**
	 * @deprecated Use {@link Robot#turnGunRight(double) turnGunRight} instead.
     *
     * @param degrees the amount of degrees to turn the robot's gun to the right.
     *    If {@code degrees} > 0 the robot's gun will turn right.
     *    If {@code degrees} < 0 the robot's gun will turn left.
     *    If {@code degrees} = 0 the robot's gun will not turn, but execute.
	 */
	@Deprecated
	public void turnGunRightDegrees(double degrees) {
		turnGunRight(degrees);
	}

	/**
	 * @deprecated Use {@link Robot#turnRadarLeft(double) turnRadarLeft} instead.
     *
     * @param degrees the amount of degrees to turn the robot's radar to the left.
     *    If {@code degrees} > 0 the robot's radar will turn left.
     *    If {@code degrees} < 0 the robot's radar will turn right.
     *    If {@code degrees} = 0 the robot's radar will not turn, but execute.
	 */
	@Deprecated
	public void turnRadarLeftDegrees(double degrees) {
		turnRadarLeft(degrees);
	}

	/**
	 * @deprecated This method is no longer functional.
	 * Use {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)} instead.
     *
     * @return allways {@code 0} as this method is no longer functional.
	 */
	@Deprecated
	public int getMaxWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}
}
