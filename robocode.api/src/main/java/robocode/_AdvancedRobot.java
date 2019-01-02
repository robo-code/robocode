/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import robocode.robotinterfaces.peer.IAdvancedRobotPeer;


/**
 * This class is used by the system, as well as being a placeholder for all deprecated
 * (meaning, you should not use them) calls for {@link AdvancedRobot}.
 * <p>
 * You should create a {@link AdvancedRobot} instead.
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
public class _AdvancedRobot extends Robot {

	_AdvancedRobot() {}

	/**
	 * @param degrees the amount of degrees to turn the robot's gun to the left.
	 *                If {@code degrees} > 0 the robot's gun is set to turn left.
	 *                If {@code degrees} < 0 the robot's gun is set to turn right.
	 *                If {@code degrees} = 0 the robot's gun is set to stop turning.
	 * @deprecated Use {@link AdvancedRobot#setTurnGunLeft(double)
	 *             setTurnGunLeft} instead.
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
	 * @param degrees the amount of degrees to turn the robot's gun to the right.
	 *                If {@code degrees} > 0 the robot's gun is set to turn right.
	 *                If {@code degrees} < 0 the robot's gun is set to turn left.
	 *                If {@code degrees} = 0 the robot's gun is set to stop turning.
	 * @deprecated Use {@link AdvancedRobot#setTurnGunRight(double)
	 *             setTurnGunRight} instead.
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
	 * @param degrees the amount of degrees to turn the robot's radar to the right.
	 *                If {@code degrees} > 0 the robot's radar will turn right.
	 *                If {@code degrees} < 0 the robot's radar will turn left.
	 *                If {@code degrees} = 0 the robot's radar will not turn, but execute.
	 * @deprecated Use {@link Robot#turnRadarRight(double) turnRadarRight}
	 *             instead.
	 */
	@Deprecated
	public void turnRadarRightDegrees(double degrees) {
		turnRadarRight(degrees);
	}

	/**
	 * @param degrees the amount of degrees to turn the robot's body to the right.
	 *                If {@code degrees} > 0 the robot is set to turn right.
	 *                If {@code degrees} < 0 the robot is set to turn left.
	 *                If {@code degrees} = 0 the robot is set to stop turning.
	 * @deprecated Use {@link AdvancedRobot#setTurnRight(double)
	 *             setTurnRight(double)} instead.
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
	 * @param degrees the amount of degrees to turn the robot's radar to the left.
	 *                If {@code degrees} > 0 the robot's radar is set to turn left.
	 *                If {@code degrees} < 0 the robot's radar is set to turn right.
	 *                If {@code degrees} = 0 the robot's radar is set to stop turning.
	 * @deprecated Use {@link AdvancedRobot#setTurnRadarLeft(double)
	 *             setTurnRadarLeft(double)} instead.
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
	 * @param degrees the amount of degrees to turn the robot's body to the left.
	 *                If {@code degrees} > 0 the robot is set to turn left.
	 *                If {@code degrees} < 0 the robot is set to turn right.
	 *                If {@code degrees} = 0 the robot is set to stop turning.
	 * @deprecated Use {@link AdvancedRobot#setTurnLeft(double)
	 *             setTurnLeft(double)} instead.
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
	 * @return the direction that the robot's body is facing, in degrees.
	 * @deprecated Use {@link Robot#getHeading() getHeading()} instead.
	 */
	@Deprecated
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * @param degrees the amount of degrees to turn the robot's body to the left.
	 *                If {@code degrees} > 0 the robot will turn left.
	 *                If {@code degrees} < 0 the robot will turn right.
	 *                If {@code degrees} = 0 the robot will not turn, but execute.
	 * @deprecated Use {@link Robot#turnLeft(double) turnLeft(double)} instead.
	 */
	@Deprecated
	public void turnLeftDegrees(double degrees) {
		turnLeft(degrees);
	}

	/**
	 * @param degrees the amount of degrees to turn the robot's body to the right.
	 *                If {@code degrees} > 0 the robot will turn right.
	 *                If {@code degrees} < 0 the robot will turn left.
	 *                If {@code degrees} = 0 the robot will not turn, but execute.
	 * @deprecated Use {@link Robot#turnRight(double) turnRight(double)} instead.
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
	 * @return the direction that the robot's gun is facing, in degrees.
	 * @deprecated Use {@link Robot#getGunHeading() getGunHeading()} instead.
	 */
	@Deprecated
	public double getGunHeadingDegrees() {
		return getGunHeading();
	}

	/**
	 * @return the direction that the robot's radar is facing, in degrees.
	 * @deprecated Use {@link Robot#getRadarHeading() getRadarHeading()} instead.
	 */
	@Deprecated
	public double getRadarHeadingDegrees() {
		return getRadarHeading();
	}

	/**
	 * @return allways {@code 0} as this method is no longer functional.
	 * @deprecated This method is no longer functional.
	 *             Use {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)} instead.
	 */
	@Deprecated
	public int getWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}

	/**
	 * @param degrees the amount of degrees to turn the robot's radar to the right.
	 *                If {@code degrees} > 0 the robot's radar is set to turn right.
	 *                If {@code degrees} < 0 the robot's radar is set to turn left.
	 *                If {@code degrees} = 0 the robot's radar is set to stop turning.
	 * @deprecated Use {@link AdvancedRobot#setTurnRadarRight(double)
	 *             setTurnRadarRight} instead.
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
	 * @param degrees the amount of degrees to turn the robot's gun to the left.
	 *                If {@code degrees} > 0 the robot's gun will turn left.
	 *                If {@code degrees} < 0 the robot's gun will turn right.
	 *                If {@code degrees} = 0 the robot's gun will not turn, but execute.
	 * @deprecated Use {@link Robot#turnGunLeft(double) turnGunLeft} instead.
	 */
	@Deprecated
	public void turnGunLeftDegrees(double degrees) {
		turnGunLeft(degrees);
	}

	/**
	 * @param degrees the amount of degrees to turn the robot's gun to the right.
	 *                If {@code degrees} > 0 the robot's gun will turn right.
	 *                If {@code degrees} < 0 the robot's gun will turn left.
	 *                If {@code degrees} = 0 the robot's gun will not turn, but execute.
	 * @deprecated Use {@link Robot#turnGunRight(double) turnGunRight} instead.
	 */
	@Deprecated
	public void turnGunRightDegrees(double degrees) {
		turnGunRight(degrees);
	}

	/**
	 * @param degrees the amount of degrees to turn the robot's radar to the left.
	 *                If {@code degrees} > 0 the robot's radar will turn left.
	 *                If {@code degrees} < 0 the robot's radar will turn right.
	 *                If {@code degrees} = 0 the robot's radar will not turn, but execute.
	 * @deprecated Use {@link Robot#turnRadarLeft(double) turnRadarLeft} instead.
	 */
	@Deprecated
	public void turnRadarLeftDegrees(double degrees) {
		turnRadarLeft(degrees);
	}

	/**
	 * @return allways {@code 0} as this method is no longer functional.
	 * @deprecated This method is no longer functional.
	 *             Use {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)} instead.
	 */
	@Deprecated
	public int getMaxWaitCount() {
		if (peer != null) {
			peer.getCall();
		}
		return 0;
	}
}
