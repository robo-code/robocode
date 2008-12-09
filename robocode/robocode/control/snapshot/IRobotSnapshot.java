/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.control.snapshot;


import robocode.peer.DebugProperty;
import robocode.peer.RobotState;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotSnapshot {

	/**
	 * Returns the name of the robot.
	 *
	 * @return the name of the robot.
	 */
	String getName();

	/**
	 * Returns the very short name of this robot.
	 *
	 * @return the very short name of this robot.
	 */
	String getShortName();

	/**
	 * Returns the very short name of this robot.
	 *
	 * @return the very short name of this robot.
	 */
	String getVeryShortName();

	/**
	 * Returns the name of the team or name of the robot if the robot is not a part of a team.
	 *
	 * @return the name of the team or name of the robot if the robot is not a part of a team.
	 */
	String getTeamName();

	/**
	 * @return Returns the index of the robot in whole battle.
	 */
	int getContestIndex();

	/**
	 * Returns the robot status.
	 *
	 * @return the robot status.
	 */
	RobotState getState();

	/**
	 * Returns the energy level.
	 *
	 * @return the energy level.
	 */
	double getEnergy();

	/**
	 * Returns the velocity.
	 *
	 * @return the velocity.
	 */
	double getVelocity();

	/**
	 * Returns the body heading in radians.
	 *
	 * @return the body heading in radians.
	 */
	double getBodyHeading();

	/**
	 * Returns the gun heading in radians.
	 *
	 * @return the gun heading in radians.
	 */
	double getGunHeading();

	/**
	 * Returns the radar heading in radians.
	 *
	 * @return the radar heading in radians.
	 */
	double getRadarHeading();

	/**
	 * Returns the gun heading in radians.
	 *
	 * @return the gun heat
	 */
	double getGunHeat();

	/**
	 * Returns the x coordinate of the robot.
	 *
	 * @return the x coordinate of the robot.
	 */
	double getX();

	/**
	 * Returns the y coordinate of the robot.
	 *
	 * @return the y coordinate of the robot.
	 */
	double getY();

	/**
	 * Returns the color of the body.
	 *
	 * @return the color of the body.
	 */
	int getBodyColor();

	/**
	 * Returns the color of the gun.
	 *
	 * @return the color of the gun.
	 */
	int getGunColor();

	/**
	 * Returns the color of the radar.
	 *
	 * @return the color of the radar.
	 */
	int getRadarColor();

	/**
	 * Returns the color of the scan arc.
	 *
	 * @return the color of the scan arc.
	 */
	int getScanColor();

	/**
	 * Returns a flag specifying if this robot is a Droid.
	 *
	 * @return {@code true} if this robot is a Droid; {@code false} otherwise.
	 */
	boolean isDroid();

	/**
	 * Returns a flag specifying if this robot is an IPaintRobot or is asking for getGraphics
	 *
	 * @return {@code true} if this robot is a an IPaintRobot or is asking for getGraphics; {@code false}
	 *         otherwise.
	 */
	boolean isPaintRobot();

	/**
	 * Returns a flag specifying if robot's (onPaint) painting is enabled for
	 * the robot.
	 *
	 * @return {@code true} if the paintings for this robot is enabled;
	 *         {@code false} otherwise.
	 */
	boolean isPaintEnabled();

	/**
	 * Returns a flag specifying if RobocodeSG painting is enabled for the
	 * robot.
	 *
	 * @return {@code true} if RobocodeSG painting is enabled for this robot;
	 *         {@code false} otherwise.
	 */
	boolean isSGPaintEnabled();

	/**
	 * @return list of debug properties
	 */
	java.util.List<DebugProperty> getDebugProperties();

	/**
	 * Returns the output print stream snapshot for this robot.
	 *
	 * @return the output print stream snapshot for this robot.
	 */
	String getOutputStreamSnapshot();

	/**
	 * Returns snapshot of score for robot
	 *
	 * @return snapshot of score for robot
	 */
	IScoreSnapshot getRobotScoreSnapshot();
}
